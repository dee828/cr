<script setup>
import {ref, reactive, onMounted, computed} from 'vue'
import {Plus, Refresh} from '@element-plus/icons-vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {saveTrain, deleteTrain, listTrain, genSeat, genDaily} from '@/api/business/train.js'

// 车次列表数据
const trainList = ref([])

// 控制对话框的显示
const dialogVisible = ref(false)

// 车次表单数据
const trainForm = reactive({
  id: null,
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
  trainForm.id = null
  trainForm.code = null
  trainForm.type = null
  trainForm.start = null
  trainForm.startPinyin = null
  trainForm.startTime = null
  trainForm.end = null
  trainForm.endPinyin = null
  trainForm.endTime = null
}

const submitForm = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    const res = await saveTrain(trainForm)
    if (res.code === 200) {
      ElMessage.success(trainForm.id ? '编辑成功' : '添加成功')
      dialogVisible.value = false
      resetForm()
      // 刷新乘客列表
      pagination.page = 1
      await getTrainList()
    } else {
      // 场景：http 返回头的状态码是 200，但是返回体中的 code 字段值不是 200
      ElMessage.error(res.msg || (trainForm.id ? '编辑车次失败' : '添加车次失败'))
    }
  } catch (error) {
    ElMessage.error(error.response.data.msg || (trainForm.id ? '编辑车次失败' : '添加车次失败'))
  }
}

const handleEdit = (row) => {
  Object.assign(trainForm, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm(
    '确定要删除车次 ' + row.id + ' 吗？',
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const res = await deleteTrain(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        // 如果当前页只有一条数据且不是第一页，删除后自动跳转到上一页
        if (trainList.value.length === 1 && pagination.page > 1) {
          pagination.page--
        }
        await getTrainList()
      } else {
        ElMessage.error(res.msg || '删除失败')
      }
    } catch (error) {
      console.error('删除车次失败:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {
    // 取消删除，不做任何操作
    console.log('取消删除，不做任何操作')
  })
}

const dialogTitle = computed(() => {
  return trainForm.id ? '编辑车次' : '新增车次'
})

const tableLoading = ref(false)

// 获取车次列表数据
const getTrainList = async () => {
  tableLoading.value = true
  try {
    const res = await listTrain({
      ...pagination,
      keyword: searchForm.keyword
    })
    if (res.code === 200) {
      trainList.value = res.data.list
      pagination.total = res.data.total
    } else {
      ElMessage.error(res.msg || '获取车次列表失败')
    }
  } catch (error) {
    console.error('获取车次列表失败:', error)
    ElMessage.error('获取车次列表失败')
  } finally {
    tableLoading.value = false
  }
}

// 在组件挂载时获取数据
onMounted(() => {
  getTrainList()
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
  await getTrainList()
}

const handleSizeChange = async (val) => {
  pagination.size = val
  pagination.page = 1
  await getTrainList()
}

const handleRefresh = () => {
  getTrainList()
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
  getTrainList()
}

// 重置搜索
const handleReset = () => {
  searchForm.keyword = ''
  handleSearch()
}

const handleGenSeat = (row) => {
  ElMessageBox.confirm(
    `确定要为车次 ${row.code} 自动生成座位吗？`,
    '操作确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const res = await genSeat(row.code)
      if (res.code === 200) {
        ElMessage.success(`为车次 ${row.code} 自动生成座位成功`)
      } else {
        ElMessage.error(res.msg || '座位生成失败')
      }
    } catch (error) {
      console.error('座位生成失败:', error)
      ElMessage.error('座位生成失败')
    }
  }).catch(() => {
    //console.log('取消操作，不做任何处理');
  })
}

const selectedDate = ref(null);
const handleGenDaily = () => {
  if (!selectedDate.value) {
    ElMessage.warning('请选择日期')
    return
  }

  ElMessageBox.confirm(
    `确定要为所有车次自动生成 ${selectedDate.value} 的每日数据吗？`,
    '操作确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const res = await genDaily(selectedDate.value)
      if (res.code === 200) {
        ElMessage.success(`为所有车次自动生成 ${selectedDate.value} 的每日数据成功`)
      } else {
        ElMessage.error(res.msg || '每日数据生成失败')
      }
    } catch (error) {
      console.error('每日数据生成失败:', error)
      ElMessage.error('每日数据生成失败')
    }
  }).catch(() => {
    //console.log('取消操作，不做任何处理');
  })
}
</script>

<template>
  <div class="train-container">
    <!-- 顶部按钮 -->
    <div class="top-tools">
      <div class="left">
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增车次</el-button>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
        <div style="margin-left: 12px">
          <el-date-picker
            v-model="selectedDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择日期"
          />
          <el-button type="warning" link @click="handleGenDaily()">生成每日数据</el-button>
        </div>
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

    <!-- 车次列表 -->
    <el-table
      :data="trainList"
      v-loading="tableLoading"
      style="width: 100%"
      border
    >
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
          <el-button type="primary" link @click="handleGenSeat(row)">生成座位</el-button>
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

    <!-- 车次对话框 -->
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
        :model="trainForm"
        :rules="rules"
        label-width="100px"
        status-icon
      >
        <el-form-item label="车次编号" prop="code">
          <el-input
            v-model="trainForm.code"
            placeholder="请输入车次编号"
            clearable
          />
        </el-form-item>
        <el-form-item label="车次类型" prop="type">
          <el-select
            v-model="trainForm.type"
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
            v-model="trainForm.start"
            placeholder="请输入始发站"
            clearable
          />
        </el-form-item>
        <el-form-item label="始发站拼音" prop="startPinyin">
          <el-input
            v-model="trainForm.startPinyin"
            placeholder="请输入始发站拼音"
            clearable
          />
        </el-form-item>
        <el-form-item label="出发时间" prop="startTime">
          <el-time-picker
            v-model="trainForm.startTime"
            placeholder="请输入出发时间"
            value-format="HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="终点站" prop="end">
          <el-input
            v-model="trainForm.end"
            placeholder="请输入终点站"
            clearable
          />
        </el-form-item>
        <el-form-item label="终点站拼音" prop="endPinyin">
          <el-input
            v-model="trainForm.endPinyin"
            placeholder="请输入终点站拼音"
            clearable
          />
        </el-form-item>
        <el-form-item label="到站时间" prop="endTime">
          <el-time-picker
            v-model="trainForm.endTime"
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
.train-container {
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
