<script setup>
import {ref, reactive, onMounted} from 'vue'
import {Refresh} from '@element-plus/icons-vue'
import {ElMessage} from 'element-plus'
import {listDailyTrainTicket} from '@/api/business/dailyTrainTicket.js'
import { useRouter } from 'vue-router'
import { useTicketStore } from '@/stores/ticket'

const ticketStore = useTicketStore()

const router = useRouter()

// 余票信息列表数据
const dailyTrainTicketList = ref([])

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

const handleBooking = (row) => {
  // 存储车票信息到 store
  ticketStore.setTicketInfo({
    id: row.id,
    date: row.date,
    trainCode: row.trainCode,
    start: row.start,
    end: row.end,
    startTime: row.startTime,
    endTime: row.endTime,
    ydz: row.ydz,
    ydzPrice: row.ydzPrice,
    edz: row.edz,
    edzPrice: row.edzPrice
  })
  // 跳转到确认页面
  router.push('/confirm')
}
</script>

<template>
  <div class="dailyTrainTicket-container">
    <!-- 顶部按钮 -->
    <div class="top-tools">
      <div class="left">
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
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button
            type="primary"
            size="small"
            @click="handleBooking(row)"
          >
            预订
          </el-button>
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
