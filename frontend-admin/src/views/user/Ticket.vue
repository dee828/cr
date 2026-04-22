<script setup>
import {ref, reactive, onMounted, computed} from 'vue'
import {Plus, Refresh} from '@element-plus/icons-vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {saveTicket, deleteTicket, listTicket} from '@/api/user/ticket.js'

// 车票记录列表数据
const ticketList = ref([])

// 控制对话框的显示
const dialogVisible = ref(false)

// 车票记录表单数据
const ticketForm = reactive({
  id: null,
  userId: 0,
  passengerId: 0,
  passengerName: null,
  trainDate: null,
  trainCode: null,
  carriageIndex: 0,
  seatRow: null,
  seatCol: null,
  startStation: null,
  startTime: null,
  endStation: null,
  endTime: null,
  seatType: null,
})

// 表单规则
const rules = {
  passengerId: [
    {required: true, message: '请输入乘客ID', trigger: 'blur'},
  ],
  passengerName: [
    {required: false, message: '请输入乘客姓名', trigger: 'blur'},
  ],
  trainDate: [
    {required: true, message: '请输入日期', trigger: 'blur'},
  ],
  trainCode: [
    {required: true, message: '请输入车次编号', trigger: 'blur'},
  ],
  carriageIndex: [
    {required: true, message: '请输入厢序', trigger: 'blur'},
  ],
  seatRow: [
    {required: true, message: '请输入排号', trigger: 'blur'},
  ],
  seatCol: [
    {required: true, message: '请输入列号', trigger: 'blur'},
  ],
  startStation: [
    {required: true, message: '请输入出发站', trigger: 'blur'},
  ],
  startTime: [
    {required: true, message: '请输入出发时间', trigger: 'blur'},
  ],
  endStation: [
    {required: true, message: '请输入到达站', trigger: 'blur'},
  ],
  endTime: [
    {required: true, message: '请输入到站时间', trigger: 'blur'},
  ],
  seatType: [
    {required: true, message: '请输入座位类型', trigger: 'blur'},
  ],
}

const formRef = ref(null)

const handleAdd = () => {
  dialogVisible.value = true
}

const resetForm = () => {
  ticketForm.id = null
  ticketForm.userId = 0
  ticketForm.passengerId = 0
  ticketForm.passengerName = null
  ticketForm.trainDate = null
  ticketForm.trainCode = null
  ticketForm.carriageIndex = 0
  ticketForm.seatRow = null
  ticketForm.seatCol = null
  ticketForm.startStation = null
  ticketForm.startTime = null
  ticketForm.endStation = null
  ticketForm.endTime = null
  ticketForm.seatType = null
}

const submitForm = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    const res = await saveTicket(ticketForm)
    if (res.code === 200) {
      ElMessage.success(ticketForm.id ? '编辑成功' : '添加成功')
      dialogVisible.value = false
      resetForm()
      // 刷新乘客列表
      pagination.page = 1
      await getTicketList()
    } else {
      // 场景：http 返回头的状态码是 200，但是返回体中的 code 字段值不是 200
      ElMessage.error(res.msg || (ticketForm.id ? '编辑车票记录失败' : '添加车票记录失败'))
    }
  } catch (error) {
    ElMessage.error(error.response.data.msg || (ticketForm.id ? '编辑车票记录失败' : '添加车票记录失败'))
  }
}

const handleEdit = (row) => {
  Object.assign(ticketForm, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm(
    '确定要删除车票记录 ' + row.id + ' 吗？',
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const res = await deleteTicket(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        // 如果当前页只有一条数据且不是第一页，删除后自动跳转到上一页
        if (ticketList.value.length === 1 && pagination.page > 1) {
          pagination.page--
        }
        await getTicketList()
      } else {
        ElMessage.error(res.msg || '删除失败')
      }
    } catch (error) {
      console.error('删除车票记录失败:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {
    // 取消删除，不做任何操作
    console.log('取消删除，不做任何操作')
  })
}

const dialogTitle = computed(() => {
  return ticketForm.id ? '编辑车票记录' : '新增车票记录'
})

const tableLoading = ref(false)

// 获取车票记录列表数据
const getTicketList = async () => {
  tableLoading.value = true
  try {
    const res = await listTicket({
      ...pagination,
      keyword: searchForm.keyword
    })
    if (res.code === 200) {
      ticketList.value = res.data.list
      pagination.total = res.data.total
    } else {
      ElMessage.error(res.msg || '获取车票记录列表失败')
    }
  } catch (error) {
    console.error('获取车票记录列表失败:', error)
    ElMessage.error('获取车票记录列表失败')
  } finally {
    tableLoading.value = false
  }
}

// 在组件挂载时获取数据
onMounted(() => {
  getTicketList()
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
  await getTicketList()
}

const handleSizeChange = async (val) => {
  pagination.size = val
  pagination.page = 1
  await getTicketList()
}

const handleRefresh = () => {
  getTicketList()
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
  getTicketList()
}

// 重置搜索
const handleReset = () => {
  searchForm.keyword = ''
  handleSearch()
}
</script>

<template>
  <div class="ticket-container">
    <!-- 顶部按钮 -->
    <div class="top-tools">
      <div class="left">
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增车票记录</el-button>
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

    <!-- 车票记录列表 -->
    <el-table
      :data="ticketList"
      v-loading="tableLoading"
      style="width: 100%"
      border
    >
      <el-table-column prop="userId" label="会员ID"/>
      <el-table-column prop="passengerId" label="乘客ID"/>
      <el-table-column prop="passengerName" label="乘客姓名"/>
      <el-table-column prop="trainDate" label="日期"/>
      <el-table-column prop="trainCode" label="车次编号"/>
      <el-table-column prop="carriageIndex" label="厢序"/>
      <el-table-column prop="seatRow" label="排号"/>
      <el-table-column prop="seatCol" label="列号"/>
      <el-table-column prop="startStation" label="出发站"/>
      <el-table-column prop="startTime" label="出发时间"/>
      <el-table-column prop="endStation" label="到达站"/>
      <el-table-column prop="endTime" label="到站时间"/>
      <el-table-column prop="seatType" label="座位类型">
        <template #default="{ row }">
          {{ getOptionLabel('seatType', row.seatType) }}
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

    <!-- 车票记录对话框 -->
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
        :model="ticketForm"
        :rules="rules"
        label-width="100px"
        status-icon
      >
        <el-form-item label="会员ID" prop="userId">
          <el-input
            v-model="ticketForm.userId"
            placeholder="请输入会员ID"
            clearable
          />
        </el-form-item>
        <el-form-item label="乘客ID" prop="passengerId">
          <el-input
            v-model="ticketForm.passengerId"
            placeholder="请输入乘客ID"
            clearable
          />
        </el-form-item>
        <el-form-item label="乘客姓名" prop="passengerName">
          <el-input
            v-model="ticketForm.passengerName"
            placeholder="请输入乘客姓名"
            clearable
          />
        </el-form-item>
        <el-form-item label="日期" prop="trainDate">
          <el-date-picker
            v-model="ticketForm.trainDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请输入日期"
          />
        </el-form-item>
        <el-form-item label="车次编号" prop="trainCode">
          <el-input
            v-model="ticketForm.trainCode"
            placeholder="请输入车次编号"
            clearable
          />
        </el-form-item>
        <el-form-item label="厢序" prop="carriageIndex">
          <el-input-number v-model="ticketForm.carriageIndex" />
        </el-form-item>
        <el-form-item label="排号" prop="seatRow">
          <el-input
            v-model="ticketForm.seatRow"
            placeholder="请输入排号"
            clearable
          />
        </el-form-item>
        <el-form-item label="列号" prop="seatCol">
          <el-input
            v-model="ticketForm.seatCol"
            placeholder="请输入列号"
            clearable
          />
        </el-form-item>
        <el-form-item label="出发站" prop="startStation">
          <el-input
            v-model="ticketForm.startStation"
            placeholder="请输入出发站"
            clearable
          />
        </el-form-item>
        <el-form-item label="出发时间" prop="startTime">
          <el-time-picker
            v-model="ticketForm.startTime"
            placeholder="请输入出发时间"
            value-format="HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="到达站" prop="endStation">
          <el-input
            v-model="ticketForm.endStation"
            placeholder="请输入到达站"
            clearable
          />
        </el-form-item>
        <el-form-item label="到站时间" prop="endTime">
          <el-time-picker
            v-model="ticketForm.endTime"
            placeholder="请输入到站时间"
            value-format="HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="座位类型" prop="seatType">
          <el-select
            v-model="ticketForm.seatType"
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
.ticket-container {
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
