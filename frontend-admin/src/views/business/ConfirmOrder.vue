<script setup>
import {ref, reactive, onMounted, computed} from 'vue'
import {Plus, Refresh} from '@element-plus/icons-vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {saveConfirmOrder, deleteConfirmOrder, listConfirmOrder} from '@/api/business/confirmOrder.js'

// 确认订单列表数据
const confirmOrderList = ref([])

// 控制对话框的显示
const dialogVisible = ref(false)

// 确认订单表单数据
const confirmOrderForm = reactive({
  id: null,
  userId: 0,
  date: null,
  trainCode: null,
  start: null,
  end: null,
  dailyTrainTicketId: 0,
  tickets: null,
  status: null,
})

// 表单规则
const rules = {
  date: [
    {required: true, message: '请输入日期', trigger: 'blur'},
  ],
  trainCode: [
    {required: true, message: '请输入车次编号', trigger: 'blur'},
  ],
  start: [
    {required: true, message: '请输入出发站', trigger: 'blur'},
  ],
  end: [
    {required: true, message: '请输入到达站', trigger: 'blur'},
  ],
  dailyTrainTicketId: [
    {required: true, message: '请输入余票ID', trigger: 'blur'},
  ],
  tickets: [
    {required: true, message: '请输入车票', trigger: 'blur'},
  ],
  status: [
    {required: true, message: '请输入订单状态', trigger: 'blur'},
  ],
}

const formRef = ref(null)

const handleAdd = () => {
  dialogVisible.value = true
}

const resetForm = () => {
  confirmOrderForm.id = null
  confirmOrderForm.userId = 0
  confirmOrderForm.date = null
  confirmOrderForm.trainCode = null
  confirmOrderForm.start = null
  confirmOrderForm.end = null
  confirmOrderForm.dailyTrainTicketId = 0
  confirmOrderForm.tickets = null
  confirmOrderForm.status = null
}

const submitForm = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    const res = await saveConfirmOrder(confirmOrderForm)
    if (res.code === 200) {
      ElMessage.success(confirmOrderForm.id ? '编辑成功' : '添加成功')
      dialogVisible.value = false
      resetForm()
      // 刷新乘客列表
      pagination.page = 1
      await getConfirmOrderList()
    } else {
      // 场景：http 返回头的状态码是 200，但是返回体中的 code 字段值不是 200
      ElMessage.error(res.msg || (confirmOrderForm.id ? '编辑确认订单失败' : '添加确认订单失败'))
    }
  } catch (error) {
    ElMessage.error(error.response.data.msg || (confirmOrderForm.id ? '编辑确认订单失败' : '添加确认订单失败'))
  }
}

const handleEdit = (row) => {
  Object.assign(confirmOrderForm, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm(
    '确定要删除确认订单 ' + row.id + ' 吗？',
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const res = await deleteConfirmOrder(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        // 如果当前页只有一条数据且不是第一页，删除后自动跳转到上一页
        if (confirmOrderList.value.length === 1 && pagination.page > 1) {
          pagination.page--
        }
        await getConfirmOrderList()
      } else {
        ElMessage.error(res.msg || '删除失败')
      }
    } catch (error) {
      console.error('删除确认订单失败:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {
    // 取消删除，不做任何操作
    console.log('取消删除，不做任何操作')
  })
}

const dialogTitle = computed(() => {
  return confirmOrderForm.id ? '编辑确认订单' : '新增确认订单'
})

const tableLoading = ref(false)

// 获取确认订单列表数据
const getConfirmOrderList = async () => {
  tableLoading.value = true
  try {
    const res = await listConfirmOrder({
      ...pagination,
      keyword: searchForm.keyword
    })
    if (res.code === 200) {
      confirmOrderList.value = res.data.list
      pagination.total = res.data.total
    } else {
      ElMessage.error(res.msg || '获取确认订单列表失败')
    }
  } catch (error) {
    console.error('获取确认订单列表失败:', error)
    ElMessage.error('获取确认订单列表失败')
  } finally {
    tableLoading.value = false
  }
}

// 在组件挂载时获取数据
onMounted(() => {
  getConfirmOrderList()
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
  await getConfirmOrderList()
}

const handleSizeChange = async (val) => {
  pagination.size = val
  pagination.page = 1
  await getConfirmOrderList()
}

const handleRefresh = () => {
  getConfirmOrderList()
}

// 订单状态选项
const statusOptions = [
  { label: '初始', value: 'I' },
  { label: '处理中', value: 'P' },
  { label: '成功', value: 'S' },
  { label: '失败', value: 'F' },
  { label: '无票', value: 'E' },
  { label: '取消', value: 'C' }
]

const optionsMap = {
  'status': statusOptions,
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
  getConfirmOrderList()
}

// 重置搜索
const handleReset = () => {
  searchForm.keyword = ''
  handleSearch()
}
</script>

<template>
  <div class="confirmOrder-container">
    <!-- 顶部按钮 -->
    <div class="top-tools">
      <div class="left">
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增确认订单</el-button>
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

    <!-- 确认订单列表 -->
    <el-table
      :data="confirmOrderList"
      v-loading="tableLoading"
      style="width: 100%"
      border
    >
      <el-table-column prop="userId" label="会员ID"/>
      <el-table-column prop="date" label="日期"/>
      <el-table-column prop="trainCode" label="车次编号"/>
      <el-table-column prop="start" label="出发站"/>
      <el-table-column prop="end" label="到达站"/>
      <el-table-column prop="dailyTrainTicketId" label="余票ID"/>
      <el-table-column prop="tickets" label="车票"/>
      <el-table-column prop="status" label="订单状态">
        <template #default="{ row }">
          {{ getOptionLabel('status', row.status) }}
        </template>
      </el-table-column>
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

    <!-- 确认订单对话框 -->
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
        :model="confirmOrderForm"
        :rules="rules"
        label-width="100px"
        status-icon
      >
        <el-form-item label="会员ID" prop="userId">
          <el-input
            v-model="confirmOrderForm.userId"
            placeholder="请输入会员ID"
            clearable
          />
        </el-form-item>
        <el-form-item label="日期" prop="date">
          <el-date-picker
            v-model="confirmOrderForm.date"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请输入日期"
          />
        </el-form-item>
        <el-form-item label="车次编号" prop="trainCode">
          <el-input
            v-model="confirmOrderForm.trainCode"
            placeholder="请输入车次编号"
            clearable
          />
        </el-form-item>
        <el-form-item label="出发站" prop="start">
          <el-input
            v-model="confirmOrderForm.start"
            placeholder="请输入出发站"
            clearable
          />
        </el-form-item>
        <el-form-item label="到达站" prop="end">
          <el-input
            v-model="confirmOrderForm.end"
            placeholder="请输入到达站"
            clearable
          />
        </el-form-item>
        <el-form-item label="余票ID" prop="dailyTrainTicketId">
          <el-input
            v-model="confirmOrderForm.dailyTrainTicketId"
            placeholder="请输入余票ID"
            clearable
          />
        </el-form-item>
        <el-form-item label="车票" prop="tickets">
          <el-input
            v-model="confirmOrderForm.tickets"
            placeholder="请输入车票"
            clearable
          />
        </el-form-item>
        <el-form-item label="订单状态" prop="status">
          <el-select
            v-model="confirmOrderForm.status"
            style="width: 100%"
            placeholder="请选择订单状态"
          >
            <el-option
              v-for="option in statusOptions"
              :key="option.value"
              :label="option.label"
              :value="option.value"
            />
          </el-select>
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
.confirmOrder-container {
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
