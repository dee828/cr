<script setup>
import {ref, reactive, onMounted, computed} from 'vue'
import {Plus, Refresh} from '@element-plus/icons-vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {saveDailyTrain, deleteDailyTrain, listDailyTrain} from '@/api/business/dailyTrain.js'

// 每日车次列表数据
const dailyTrainList = ref([])

// 控制对话框的显示
const dialogVisible = ref(false)

// 每日车次表单数据
const dailyTrainForm = reactive({
  id: null,
  date: null,
  code: null,
  type: null,
  start: null,
  startPinyin: null,
  startTime: null,
  end: null,
  endPinyin: null,
  endTime: null,
})

// 表单规则
const rules = {
  date: [
    {required: true, message: '请输入日期', trigger: 'blur'},
  ],
  code: [
    {required: true, message: '请输入车次编号', trigger: 'blur'},
  ],
  type: [
    {required: true, message: '请输入车次类型', trigger: 'blur'},
  ],
  start: [
    {required: true, message: '请输入始发站', trigger: 'blur'},
  ],
  startPinyin: [
    {required: true, message: '请输入始发站拼音', trigger: 'blur'},
  ],
  startTime: [
    {required: true, message: '请输入出发时间', trigger: 'blur'},
  ],
  end: [
    {required: true, message: '请输入终点站', trigger: 'blur'},
  ],
  endPinyin: [
    {required: true, message: '请输入终点站拼音', trigger: 'blur'},
  ],
  endTime: [
    {required: true, message: '请输入到站时间', trigger: 'blur'},
  ],
}

const formRef = ref(null)

const handleAdd = () => {
  dialogVisible.value = true
}

const resetForm = () => {
  dailyTrainForm.id = null
  dailyTrainForm.date = null
  dailyTrainForm.code = null
  dailyTrainForm.type = null
  dailyTrainForm.start = null
  dailyTrainForm.startPinyin = null
  dailyTrainForm.startTime = null
  dailyTrainForm.end = null
  dailyTrainForm.endPinyin = null
  dailyTrainForm.endTime = null
}

const submitForm = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    const res = await saveDailyTrain(dailyTrainForm)
    if (res.code === 200) {
      ElMessage.success(dailyTrainForm.id ? '编辑成功' : '添加成功')
      dialogVisible.value = false
      resetForm()
      // 刷新乘客列表
      pagination.page = 1
      await getDailyTrainList()
    } else {
      // 场景：http 返回头的状态码是 200，但是返回体中的 code 字段值不是 200
      ElMessage.error(res.msg || (dailyTrainForm.id ? '编辑每日车次失败' : '添加每日车次失败'))
    }
  } catch (error) {
    ElMessage.error(error.response.data.msg || (dailyTrainForm.id ? '编辑每日车次失败' : '添加每日车次失败'))
  }
}

const handleEdit = (row) => {
  Object.assign(dailyTrainForm, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm(
    '确定要删除每日车次 ' + row.id + ' 吗？',
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const res = await deleteDailyTrain(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        // 如果当前页只有一条数据且不是第一页，删除后自动跳转到上一页
        if (dailyTrainList.value.length === 1 && pagination.page > 1) {
          pagination.page--
        }
        await getDailyTrainList()
      } else {
        ElMessage.error(res.msg || '删除失败')
      }
    } catch (error) {
      console.error('删除每日车次失败:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {
    // 取消删除，不做任何操作
    console.log('取消删除，不做任何操作')
  })
}

const dialogTitle = computed(() => {
  return dailyTrainForm.id ? '编辑每日车次' : '新增每日车次'
})

const tableLoading = ref(false)

// 获取每日车次列表数据
const getDailyTrainList = async () => {
  tableLoading.value = true
  try {
    const res = await listDailyTrain({
      ...pagination,
      keyword: searchForm.keyword
    })
    if (res.code === 200) {
      dailyTrainList.value = res.data.list
      pagination.total = res.data.total
    } else {
      ElMessage.error(res.msg || '获取每日车次列表失败')
    }
  } catch (error) {
    console.error('获取每日车次列表失败:', error)
    ElMessage.error('获取每日车次列表失败')
  } finally {
    tableLoading.value = false
  }
}

// 在组件挂载时获取数据
onMounted(() => {
  getDailyTrainList()
})

// 分页相关数据
const pagination = reactive({
  // 每页显示的数据条数
  size: 10,
  // 当前页码，从1开始
  page: 1,
  // 总数据条数，会在获取数据时更新
  total: 0
})

// 分页变化处理函数
const handleCurrentChange = async (val) => {
  pagination.page = val
  await getDailyTrainList()
}

const handleSizeChange = async (val) => {
  pagination.size = val
  pagination.page = 1
  await getDailyTrainList()
}

const handleRefresh = () => {
  getDailyTrainList()
}

// 车次类型选项
const typeOptions = [
  { label: '高铁', value: 'G' },
  { label: '动车', value: 'D' },
  { label: '快速', value: 'K' }
]

const optionsMap = {
  'type': typeOptions,
}

// 获取枚举字段的显示标签
const getOptionLabel = (fieldName, value) => {
  if (value === null || value === undefined || value === '') return '-'
  const options = optionsMap[fieldName]
  if (!options) return String(value)
  const option = options.find(opt => opt.value === String(value))
  return option?.label || String(value)
}

// 搜索表单
const searchForm = reactive({
  keyword: ''  // 搜索关键字
})

// 处理搜索
const handleSearch = () => {
  pagination.page = 1  // 重置到第一页
  getDailyTrainList()
}

// 重置搜索
const handleReset = () => {
  searchForm.keyword = ''
  handleSearch()
}
</script>

<template>
  <div class="dailyTrain-container">
    <!-- 顶部按钮 -->
    <div class="top-tools">
      <div class="left">
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增每日车次</el-button>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </div>
      <div class="right">
        <el-form :inline="true" :model="searchForm" @submit.prevent>
          <el-form-item class="mb0">
            <el-input
                    v-model="searchForm.keyword"
                    placeholder="请输入关键词"
                    clearable
            />
          </el-form-item>
          <el-form-item class="mb0">
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <!-- 每日车次列表 -->
    <el-table
      :data="dailyTrainList"
      v-loading="tableLoading"
      style="width: 100%"
      border
    >
      <el-table-column prop="date" label="日期"/>
      <el-table-column prop="code" label="车次编号"/>
      <el-table-column prop="type" label="车次类型">
        <template #default="{ row }">
          {{ getOptionLabel('type', row.type) }}
        </template>
      </el-table-column>
      <el-table-column prop="start" label="始发站"/>
      <el-table-column prop="startPinyin" label="始发站拼音"/>
      <el-table-column prop="startTime" label="出发时间"/>
      <el-table-column prop="end" label="终点站"/>
      <el-table-column prop="endPinyin" label="终点站拼音"/>
      <el-table-column prop="endTime" label="到站时间"/>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :page-sizes="[2, 5, 10, 20]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 每日车次对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      destroy-on-close
      @closed="resetForm"
    >
      <el-form
        ref="formRef"
        :model="dailyTrainForm"
        :rules="rules"
        label-width="100px"
        status-icon
      >
        <el-form-item label="日期" prop="date">
          <el-date-picker
            v-model="dailyTrainForm.date"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请输入日期"
          />
        </el-form-item>
        <el-form-item label="车次编号" prop="code">
          <el-input
            v-model="dailyTrainForm.code"
            placeholder="请输入车次编号"
            clearable
          />
        </el-form-item>
        <el-form-item label="车次类型" prop="type">
          <el-select
            v-model="dailyTrainForm.type"
            style="width: 100%"
            placeholder="请选择车次类型"
          >
            <el-option
              v-for="option in typeOptions"
              :key="option.value"
              :label="option.label"
              :value="option.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="始发站" prop="start">
          <el-input
            v-model="dailyTrainForm.start"
            placeholder="请输入始发站"
            clearable
          />
        </el-form-item>
        <el-form-item label="始发站拼音" prop="startPinyin">
          <el-input
            v-model="dailyTrainForm.startPinyin"
            placeholder="请输入始发站拼音"
            clearable
          />
        </el-form-item>
        <el-form-item label="出发时间" prop="startTime">
          <el-time-picker
            v-model="dailyTrainForm.startTime"
            placeholder="请输入出发时间"
            value-format="HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="终点站" prop="end">
          <el-input
            v-model="dailyTrainForm.end"
            placeholder="请输入终点站"
            clearable
          />
        </el-form-item>
        <el-form-item label="终点站拼音" prop="endPinyin">
          <el-input
            v-model="dailyTrainForm.endPinyin"
            placeholder="请输入终点站拼音"
            clearable
          />
        </el-form-item>
        <el-form-item label="到站时间" prop="endTime">
          <el-time-picker
            v-model="dailyTrainForm.endTime"
            placeholder="请输入到站时间"
            value-format="HH:mm:ss"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="submitForm">确 定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.dailyTrain-container {
  padding: 20px;
}

.top-tools {
  margin-bottom: 20px;
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  align-items: center;
}

.top-tools .left {
  display: flex;
  gap: 10px;
}

.top-tools .right {
  display: flex;
  align-items: center;
  margin-left: auto;

  .el-form .el-form-item:last-child {
    margin-right: 0;
  }
}

@media screen and (max-width: 768px) {
  .top-tools {
    flex-direction: column;
    align-items: flex-start;
  }

  .top-tools .right {
    margin-left: 0;
    width: 100%;
  }
}

.mb0 {
  margin-bottom: 0;
}

.el-form--inline .el-form-item {
  margin-right: 16px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

:deep(.el-scrollbar) {
  margin-bottom: 0;
}
</style>
