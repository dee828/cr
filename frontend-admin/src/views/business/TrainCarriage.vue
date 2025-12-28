<script setup>
import {ref, reactive, onMounted, computed} from 'vue'
import {Plus, Refresh} from '@element-plus/icons-vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {saveTrainCarriage, deleteTrainCarriage, listTrainCarriage} from '@/api/business/trainCarriage.js'

// 火车车厢列表数据
const trainCarriageList = ref([])

// 控制对话框的显示
const dialogVisible = ref(false)

// 火车车厢表单数据
const trainCarriageForm = reactive({
  id: null,
  trainCode: null,
  index: 0,
  seatType: null,
  seatCount: 0,
  rowCount: 0,
  colCount: 0,
})

// 表单规则
const rules = {
  trainCode: [
    {required: true, message: '请输入车次编号', trigger: 'blur'},
  ],
  index: [
    {required: true, message: '请输入厢号', trigger: 'blur'},
  ],
  seatType: [
    {required: true, message: '请输入座位类型', trigger: 'blur'},
  ],
  seatCount: [
    {required: true, message: '请输入座位数', trigger: 'blur'},
  ],
  rowCount: [
    {required: true, message: '请输入排数', trigger: 'blur'},
  ],
  colCount: [
    {required: true, message: '请输入列数', trigger: 'blur'},
  ],
}

const formRef = ref(null)

const handleAdd = () => {
  dialogVisible.value = true
}

const resetForm = () => {
  trainCarriageForm.id = null
  trainCarriageForm.trainCode = null
  trainCarriageForm.index = 0
  trainCarriageForm.seatType = null
  trainCarriageForm.seatCount = 0
  trainCarriageForm.rowCount = 0
  trainCarriageForm.colCount = 0
}

const submitForm = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    const res = await saveTrainCarriage(trainCarriageForm)
    if (res.code === 200) {
      ElMessage.success(trainCarriageForm.id ? '编辑成功' : '添加成功')
      dialogVisible.value = false
      resetForm()
      // 刷新乘客列表
      pagination.page = 1
      await getTrainCarriageList()
    } else {
      // 场景：http 返回头的状态码是 200，但是返回体中的 code 字段值不是 200
      ElMessage.error(res.msg || (trainCarriageForm.id ? '编辑火车车厢失败' : '添加火车车厢失败'))
    }
  } catch (error) {
    ElMessage.error(error.response.data.msg || (trainCarriageForm.id ? '编辑火车车厢失败' : '添加火车车厢失败'))
  }
}

const handleEdit = (row) => {
  Object.assign(trainCarriageForm, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm(
    '确定要删除火车车厢 ' + row.id + ' 吗？',
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const res = await deleteTrainCarriage(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        // 如果当前页只有一条数据且不是第一页，删除后自动跳转到上一页
        if (trainCarriageList.value.length === 1 && pagination.page > 1) {
          pagination.page--
        }
        await getTrainCarriageList()
      } else {
        ElMessage.error(res.msg || '删除失败')
      }
    } catch (error) {
      console.error('删除火车车厢失败:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {
    // 取消删除，不做任何操作
    console.log('取消删除，不做任何操作')
  })
}

const dialogTitle = computed(() => {
  return trainCarriageForm.id ? '编辑火车车厢' : '新增火车车厢'
})

const tableLoading = ref(false)

// 获取火车车厢列表数据
const getTrainCarriageList = async () => {
  tableLoading.value = true
  try {
    const res = await listTrainCarriage({
      ...pagination,
      keyword: searchForm.keyword
    })
    if (res.code === 200) {
      trainCarriageList.value = res.data.list
      pagination.total = res.data.total
    } else {
      ElMessage.error(res.msg || '获取火车车厢列表失败')
    }
  } catch (error) {
    console.error('获取火车车厢列表失败:', error)
    ElMessage.error('获取火车车厢列表失败')
  } finally {
    tableLoading.value = false
  }
}

// 在组件挂载时获取数据
onMounted(() => {
  getTrainCarriageList()
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
  await getTrainCarriageList()
}

const handleSizeChange = async (val) => {
  pagination.size = val
  pagination.page = 1
  await getTrainCarriageList()
}

const handleRefresh = () => {
  getTrainCarriageList()
}

// 座位类型选项
const seatTypeOptions = [
  { label: '一等座', value: '1' },
  { label: '二等座', value: '2' }
]

const optionsMap = {
  'seatType': seatTypeOptions,
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
  getTrainCarriageList()
}

// 重置搜索
const handleReset = () => {
  searchForm.keyword = ''
  handleSearch()
}
</script>

<template>
  <div class="trainCarriage-container">
    <!-- 顶部按钮 -->
    <div class="top-tools">
      <div class="left">
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增火车车厢</el-button>
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

    <!-- 火车车厢列表 -->
    <el-table
      :data="trainCarriageList"
      v-loading="tableLoading"
      style="width: 100%"
      border
    >
      <el-table-column prop="trainCode" label="车次编号"/>
      <el-table-column prop="index" label="厢号"/>
      <el-table-column prop="seatType" label="座位类型">
        <template #default="{ row }">
          {{ getOptionLabel('seatType', row.seatType) }}
        </template>
      </el-table-column>
      <el-table-column prop="seatCount" label="座位数"/>
      <el-table-column prop="rowCount" label="排数"/>
      <el-table-column prop="colCount" label="列数"/>
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

    <!-- 火车车厢对话框 -->
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
        :model="trainCarriageForm"
        :rules="rules"
        label-width="100px"
        status-icon
      >
        <el-form-item label="车次编号" prop="trainCode">
          <el-input
            v-model="trainCarriageForm.trainCode"
            placeholder="请输入车次编号"
            clearable
          />
        </el-form-item>
        <el-form-item label="厢号" prop="index">
          <el-input-number v-model="trainCarriageForm.index" />
        </el-form-item>
        <el-form-item label="座位类型" prop="seatType">
          <el-select
            v-model="trainCarriageForm.seatType"
            style="width: 100%"
            placeholder="请选择座位类型"
          >
            <el-option
              v-for="option in seatTypeOptions"
              :key="option.value"
              :label="option.label"
              :value="option.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="座位数" prop="seatCount">
          <el-input-number v-model="trainCarriageForm.seatCount" />
        </el-form-item>
        <el-form-item label="排数" prop="rowCount">
          <el-input-number v-model="trainCarriageForm.rowCount" />
        </el-form-item>
        <el-form-item label="列数" prop="colCount">
          <el-input-number v-model="trainCarriageForm.colCount" />
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
.trainCarriage-container {
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
