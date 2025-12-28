<script setup>
import {ref, reactive, onMounted, computed} from 'vue'
import {Plus, Refresh} from '@element-plus/icons-vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {saveDailyTrainTicket, deleteDailyTrainTicket, listDailyTrainTicket} from '@/api/business/dailyTrainTicket.js'

// 余票信息列表数据
const dailyTrainTicketList = ref([])

// 控制对话框的显示
const dialogVisible = ref(false)

// 余票信息表单数据
const dailyTrainTicketForm = reactive({
  id: null,
  date: null,
  trainCode: null,
  start: null,
  startPinyin: null,
  startTime: null,
  startIndex: 0,
  end: null,
  endPinyin: null,
  endTime: null,
  endIndex: 0,
  ydz: 0,
  ydzPrice: 0.0,
  edz: 0,
  edzPrice: 0.0,
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
  startPinyin: [
    {required: true, message: '请输入出发站拼音', trigger: 'blur'},
  ],
  startTime: [
    {required: true, message: '请输入出发时间', trigger: 'blur'},
  ],
  startIndex: [
    {required: true, message: '请输入出发站序', trigger: 'blur'},
  ],
  end: [
    {required: true, message: '请输入到达站', trigger: 'blur'},
  ],
  endPinyin: [
    {required: true, message: '请输入到达站拼音', trigger: 'blur'},
  ],
  endTime: [
    {required: true, message: '请输入到站时间', trigger: 'blur'},
  ],
  endIndex: [
    {required: true, message: '请输入到站站序', trigger: 'blur'},
  ],
  ydz: [
    {required: true, message: '请输入一等座余票', trigger: 'blur'},
  ],
  ydzPrice: [
    {required: true, message: '请输入一等座票价', trigger: 'blur'},
  ],
  edz: [
    {required: true, message: '请输入二等座余票', trigger: 'blur'},
  ],
  edzPrice: [
    {required: true, message: '请输入二等座票价', trigger: 'blur'},
  ],
}

const formRef = ref(null)

const handleAdd = () => {
  dialogVisible.value = true
}

const resetForm = () => {
  dailyTrainTicketForm.id = null
  dailyTrainTicketForm.date = null
  dailyTrainTicketForm.trainCode = null
  dailyTrainTicketForm.start = null
  dailyTrainTicketForm.startPinyin = null
  dailyTrainTicketForm.startTime = null
  dailyTrainTicketForm.startIndex = 0
  dailyTrainTicketForm.end = null
  dailyTrainTicketForm.endPinyin = null
  dailyTrainTicketForm.endTime = null
  dailyTrainTicketForm.endIndex = 0
  dailyTrainTicketForm.ydz = 0
  dailyTrainTicketForm.ydzPrice = 0.0
  dailyTrainTicketForm.edz = 0
  dailyTrainTicketForm.edzPrice = 0.0
}

const submitForm = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    const res = await saveDailyTrainTicket(dailyTrainTicketForm)
    if (res.code === 200) {
      ElMessage.success(dailyTrainTicketForm.id ? '编辑成功' : '添加成功')
      dialogVisible.value = false
      resetForm()
      // 刷新乘客列表
      pagination.page = 1
      await getDailyTrainTicketList()
    } else {
      // 场景：http 返回头的状态码是 200，但是返回体中的 code 字段值不是 200
      ElMessage.error(res.msg || (dailyTrainTicketForm.id ? '编辑余票信息失败' : '添加余票信息失败'))
    }
  } catch (error) {
    ElMessage.error(error.response.data.msg || (dailyTrainTicketForm.id ? '编辑余票信息失败' : '添加余票信息失败'))
  }
}

const handleEdit = (row) => {
  Object.assign(dailyTrainTicketForm, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm(
    '确定要删除余票信息 ' + row.id + ' 吗？',
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const res = await deleteDailyTrainTicket(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        // 如果当前页只有一条数据且不是第一页，删除后自动跳转到上一页
        if (dailyTrainTicketList.value.length === 1 && pagination.page > 1) {
          pagination.page--
        }
        await getDailyTrainTicketList()
      } else {
        ElMessage.error(res.msg || '删除失败')
      }
    } catch (error) {
      console.error('删除余票信息失败:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {
    // 取消删除，不做任何操作
    console.log('取消删除，不做任何操作')
  })
}

const dialogTitle = computed(() => {
  return dailyTrainTicketForm.id ? '编辑余票信息' : '新增余票信息'
})

const tableLoading = ref(false)

// 获取余票信息列表数据
const getDailyTrainTicketList = async () => {
  tableLoading.value = true
  try {
    const res = await listDailyTrainTicket({
      ...pagination,
      keyword: searchForm.keyword
    })
    if (res.code === 200) {
      dailyTrainTicketList.value = res.data.list
      pagination.total = res.data.total
    } else {
      ElMessage.error(res.msg || '获取余票信息列表失败')
    }
  } catch (error) {
    console.error('获取余票信息列表失败:', error)
    ElMessage.error('获取余票信息列表失败')
  } finally {
    tableLoading.value = false
  }
}

// 在组件挂载时获取数据
onMounted(() => {
  getDailyTrainTicketList()
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
  await getDailyTrainTicketList()
}

const handleSizeChange = async (val) => {
  pagination.size = val
  pagination.page = 1
  await getDailyTrainTicketList()
}

const handleRefresh = () => {
  getDailyTrainTicketList()
}

// 搜索表单
const searchForm = reactive({
  keyword: ''  // 搜索关键字
})

// 处理搜索
const handleSearch = () => {
  pagination.page = 1  // 重置到第一页
  getDailyTrainTicketList()
}

// 重置搜索
const handleReset = () => {
  searchForm.keyword = ''
  handleSearch()
}


// 计算行程时间
const calculateDuration = (startTime, endTime) => {
  const getMinutes = (time) => {
    const [hours, minutes, seconds] = time.split(':').map(Number)
    return hours * 3600 + minutes * 60 + seconds
  }

  let startMinutes = getMinutes(startTime)
  let endMinutes = getMinutes(endTime)

  // 如果结束时间小于开始时间，说明是跨天，需要加24小时
  if (endMinutes < startMinutes) {
    endMinutes += 24 * 3600
  }

  // 计算时间差
  const diffSeconds = endMinutes - startMinutes
  const hours = Math.floor(diffSeconds / 3600)
  const minutes = Math.floor((diffSeconds % 3600) / 60)
  const seconds = diffSeconds % 60

  return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`
}

// 判断是否跨天
const isNextDay = (startTime, endTime) => {
  const getMinutes = (time) => {
    const [hours, minutes, seconds] = time.split(':').map(Number)
    return hours * 3600 + minutes * 60 + seconds
  }
  return getMinutes(endTime) < getMinutes(startTime)
}
</script>

<template>
  <div class="dailyTrainTicket-container">
    <!-- 顶部按钮 -->
    <div class="top-tools">
      <div class="left">
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增余票信息</el-button>
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

    <!-- 余票信息列表 -->
    <el-table
      ref="tableRef"
      :data="dailyTrainTicketList"
      v-loading="tableLoading"
      style="width: 100%"
      border
    >
      <el-table-column prop="date" label="日期" width="120"/>
      <el-table-column prop="trainCode" label="车次编号" width="100"/>
      <el-table-column label="车站" min-width="120">
        <template #default="{ row }">
          <div class="two-line">
            <div data-label="出发站：">{{ row.start }}</div>
            <div data-label="到达站：">{{ row.end }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="时间" min-width="150">
        <template #default="{ row }">
          <div class="two-line">
            <div data-label="出发时间：">{{ row.startTime }}</div>
            <div data-label="到达时间：">{{ row.endTime }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="历时" min-width="120">
        <template #default="{ row }">
          <div class="two-line">
            <div data-label="总时长：">{{ calculateDuration(row.startTime, row.endTime) }}</div>
            <div data-label="到达日：">{{
                isNextDay(row.startTime, row.endTime) ? '次日到达' : '当日到达'
              }}
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="一等座" min-width="120">
        <template #default="{ row }">
          <div class="two-line" v-if="row.ydz !== -1">
            <div data-label="余票：">{{ row.ydz }}</div>
            <div data-label="票价：">{{ row.ydzPrice }}¥</div>
          </div>
          <div class="unavailable" v-else>--</div>
        </template>
      </el-table-column>
      <el-table-column label="二等座" min-width="120">
        <template #default="{ row }">
          <div class="two-line" v-if="row.edz !== -1">
            <div data-label="余票：">{{ row.edz }}</div>
            <div data-label="票价：">{{ row.edzPrice }}¥</div>
          </div>
          <div class="unavailable" v-else>--</div>
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

    <!-- 余票信息对话框 -->
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
        :model="dailyTrainTicketForm"
        :rules="rules"
        label-width="100px"
        status-icon
      >
        <el-form-item label="日期" prop="date">
          <el-date-picker
            v-model="dailyTrainTicketForm.date"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请输入日期"
          />
        </el-form-item>
        <el-form-item label="车次编号" prop="trainCode">
          <el-input
            v-model="dailyTrainTicketForm.trainCode"
            placeholder="请输入车次编号"
            clearable
          />
        </el-form-item>
        <el-form-item label="出发站" prop="start">
          <el-input
            v-model="dailyTrainTicketForm.start"
            placeholder="请输入出发站"
            clearable
          />
        </el-form-item>
        <el-form-item label="出发站拼音" prop="startPinyin">
          <el-input
            v-model="dailyTrainTicketForm.startPinyin"
            placeholder="请输入出发站拼音"
            clearable
          />
        </el-form-item>
        <el-form-item label="出发时间" prop="startTime">
          <el-time-picker
            v-model="dailyTrainTicketForm.startTime"
            placeholder="请输入出发时间"
            value-format="HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="出发站序" prop="startIndex">
          <el-input-number v-model="dailyTrainTicketForm.startIndex" />
        </el-form-item>
        <el-form-item label="到达站" prop="end">
          <el-input
            v-model="dailyTrainTicketForm.end"
            placeholder="请输入到达站"
            clearable
          />
        </el-form-item>
        <el-form-item label="到达站拼音" prop="endPinyin">
          <el-input
            v-model="dailyTrainTicketForm.endPinyin"
            placeholder="请输入到达站拼音"
            clearable
          />
        </el-form-item>
        <el-form-item label="到站时间" prop="endTime">
          <el-time-picker
            v-model="dailyTrainTicketForm.endTime"
            placeholder="请输入到站时间"
            value-format="HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="到站站序" prop="endIndex">
          <el-input-number v-model="dailyTrainTicketForm.endIndex" />
        </el-form-item>
        <el-form-item label="一等座余票" prop="ydz">
          <el-input-number v-model="dailyTrainTicketForm.ydz" />
        </el-form-item>
        <el-form-item label="一等座票价" prop="ydzPrice">
          <el-input-number v-model="dailyTrainTicketForm.ydzPrice" :precision="2" />
        </el-form-item>
        <el-form-item label="二等座余票" prop="edz">
          <el-input-number v-model="dailyTrainTicketForm.edz" />
        </el-form-item>
        <el-form-item label="二等座票价" prop="edzPrice">
          <el-input-number v-model="dailyTrainTicketForm.edzPrice" :precision="2" />
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
.dailyTrainTicket-container {
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

.two-line {
  display: flex;
  flex-direction: column;
  line-height: 1.5;
}

.two-line > div {
  width: 100%;
}

/* 辅助描述文字样式 */
.two-line > div::before {
  content: attr(data-label);
  font-size: 12px;
  color: #909399;
  margin-right: 4px;
}

.unavailable {
  color: #999;
  height: 48px;
  line-height: 48px;
}

/* 让表格行高更适合显示两行内容 */
:deep(.el-table__row) {
  height: 60px;
}
</style>
