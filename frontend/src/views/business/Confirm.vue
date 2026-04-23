<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { ArrowRight } from '@element-plus/icons-vue'
import { useTicketStore } from '@/stores/ticket'
import { router } from "@/router/index.js";
import { listPassenger } from '@/api/user/passenger.js'
import { confirmOrder } from '@/api/business/dailyTrainTicket.js'

const ticketStore = useTicketStore()

const ticketInfo = ref({})
const selectedSeat = ref('')
const loading = ref(false)

// 座位类型选项
const seatOptions = [
  { label: '一等座', value: '1', price: 0, count: 0 },
  { label: '二等座', value: '2', price: 0, count: 0 },
]

// 乘客类型选项
const typeOptions = [
  { label: '成人', value: '1' },
  { label: '儿童', value: '2' },
  { label: '学生', value: '3' }
]

// 乘客相关数据
const passengerList = ref([])
const selectedPassengers = ref([])
const passengerLoading = ref(false)
// 定义常量
const MAX_PASSENGER_COUNT = 2  // 最大乘客选择数量
const MIN_SEAT_COUNT_FOR_SELECTION = 20  // 支持选座的最小余票数量（余票过少时，不太可能选到理想的座位）

onMounted(() => {
  // 从 store 获取车票信息
  if (!ticketStore.ticketInfo) {
    ElMessage.error('车票信息不存在')
    router.push('/daily-train-ticket')
    return
  }

  ticketInfo.value = ticketStore.ticketInfo

  // 更新座位选项的价格和余票，并过滤掉余票为-1的选项
  seatOptions[0].price = ticketInfo.value.ydzPrice
  seatOptions[0].count = ticketInfo.value.ydz
  seatOptions[1].price = ticketInfo.value.edzPrice
  seatOptions[1].count = ticketInfo.value.edz

  getPassengerList()
})

// 获取乘客列表
const getPassengerList = async () => {
  passengerLoading.value = true
  try {
    const res = await listPassenger({
      page: 1,
      size: 20
    })
    if (res.code === 200) {
      passengerList.value = res.data.list
    } else {
      ElMessage.error(res.msg || '获取乘客列表失败')
    }
  } catch (error) {
    console.error('获取乘客列表失败:', error)
    ElMessage.error('获取乘客列表失败')
  } finally {
    passengerLoading.value = false
  }
}

const handleSubmit = () => {
  if (!selectedSeat.value) {
    ElMessage.warning('请选择座位类型')
    return
  }
  if (selectedPassengers.value.length === 0) {
    ElMessage.warning('请选择乘车人')
    return
  }

  // 检查选中座位类型的余票是否足够
  const seatOption = seatOptions.find(option => option.value === selectedSeat.value)
  console.log('已选择的座位类型', seatOption)
  console.log('已选择的乘车人数量', selectedPassengers.value.length)
  if (seatOption && seatOption.count < selectedPassengers.value.length) {
    ElMessage.error(`${seatOption.label}余票不足，仅剩${seatOption.count}张`)
    return
  }

  // 检查余票充足后，判断是否支持选座及选座类型
  const chooseSeatType = getChooseSeatType()

  // 准备确认信息
  confirmInfo.value = {
    passengers: selectedPassengers.value.map(id => {
      const passenger = passengerList.value.find(p => p.id === id)
      return {
        passengerId: id,
        passengerName: passenger.name,
        passengerType: passenger.type,
        passengerIdCard: passenger.idCard,
        seatTypeCode: selectedSeat.value,
      }
    }),
    chooseSeatType,
    seats: chooseSeatType > 0 ? getInitialSeats(chooseSeatType) : null
  }
  console.log('订单确认信息:', confirmInfo.value)

  // 显示确认弹出层
  dialogVisible.value = true;
}

// 监听乘客选择变化
const handlePassengerChange = (values) => {
  if (values.length > MAX_PASSENGER_COUNT) {
    ElMessage.warning(`每次最多选择${MAX_PASSENGER_COUNT}位乘车人`)
    // 只保留前 MAX_PASSENGER_COUNT 个选择的乘客
    selectedPassengers.value = values.slice(0, MAX_PASSENGER_COUNT)
  }
}

// 获取座位显示名称
const getSeatLabel = (row, col) => `${col}${row}`

// 处理座位点击
const handleSeatClick = (row, col) => {
  const seatKey = getSeatLabel(row, col)
  const index = selectedSeats.value.indexOf(seatKey)

  // 找到对应的座位对象
  const seat = confirmInfo.value.seats[row - 1].find(s => s.col === col)

  // 如果已选中，则取消选中
  if (index > -1) {
    selectedSeats.value.splice(index, 1)
    seat.selected = false
    return
  }

  // 如果未选满，则选中
  if (selectedSeats.value.length < confirmInfo.value.passengers.length) {
    selectedSeats.value.push(seatKey)
    seat.selected = true
  }
}

// 计算行程时间
const calculateDuration = (startTime, endTime) => {
  if (!startTime || !endTime) return ''

  const getMinutes = (time) => {
    const [hours, minutes] = time.split(':').map(Number)
    return hours * 60 + minutes
  }

  let startMinutes = getMinutes(startTime)
  let endMinutes = getMinutes(endTime)

  // 如果结束时间小于开始时间，说明是跨天，需要加24小时
  if (endMinutes < startMinutes) {
    endMinutes += 24 * 60
  }

  const diffMinutes = endMinutes - startMinutes
  const hours = Math.floor(diffMinutes / 60)
  const minutes = diffMinutes % 60

  return `${hours}小时${minutes}分钟`
}

// 判断是否跨天
const isNextDay = (startTime, endTime) => {
  if (!startTime || !endTime) return false

  const getMinutes = (time) => {
    const [hours, minutes] = time.split(':').map(Number)
    return hours * 60 + minutes
  }
  return getMinutes(endTime) < getMinutes(startTime)
}

// 添加弹出层相关的数据
const dialogVisible = ref(false)
const confirmInfo = ref({
  passengers: [],
  chooseSeatType: 0, // 选座的座位类型（根据它展示不同选座界面）：1=一等座；2=二等座；0=不支持选座
  seats: null
})

// 判断是否支持选座并返回座位类型
const getChooseSeatType = () => {
  // 检查余票数量是否足够支持选座
  const seatOption = seatOptions.find(option => option.value === selectedSeat.value)
  if (!seatOption || seatOption.count < MIN_SEAT_COUNT_FOR_SELECTION) {
    return 0 // 余票过少，不支持选座
  }

  // 1 or 2
  return selectedSeat.value
}

// 构造初始座位数据
const SEAT_COL_ARRAY = [{code:"A", desc:"A", type:"1"}, {code:"C", desc:"C", type:"1"}, {code:"D", desc:"D", type:"1"}, {code:"F", desc:"F", type:"1"}, {code:"A", desc:"A", type:"2"}, {code:"B", desc:"B", type:"2"}, {code:"C", desc:"C", type:"2"}, {code:"D", desc:"D", type:"2"}, {code:"F", desc:"F", type:"2"}];
const getInitialSeats = (chooseSeatType) => {
  // 获取座位列信息
  const cols = SEAT_COL_ARRAY
    .filter(item => item.type === chooseSeatType.toString())
    .map(item => item.code)

  // 构造两排座位
  return [1, 2].map(row => {
    return cols.map(col => ({
      row,
      col,
      selected: false  // 初始都未选中
    }))
  })
}

// 选中的座位
const selectedSeats = ref([])  // 存储选中的座位，格式：['A1', 'C1']

// 取消订单
const handleCancel = () => {
  // 清空选座数据
  selectedSeats.value = []
  if (confirmInfo.value.seats) {
    // 重置所有座位的选中状态
    confirmInfo.value.seats.forEach(row => {
      row.forEach(seat => {
        seat.selected = false
      })
    })
  }
  dialogVisible.value = false
}

const confirmLoading = ref(false)
// 确认订单
const handleConfirm = () => {
  // 如果是选座模式，且已经选择了座位，则校验座位数量
  if (confirmInfo.value.chooseSeatType > 0 && selectedSeats.value.length > 0) {
    if (selectedSeats.value.length !== confirmInfo.value.passengers.length) {
      ElMessage.warning(`您已选择${selectedSeats.value.length}个座位，请继续选择或取消已选座位`)
      return
    }
  }

  confirmLoading.value = true

  const confirmData = {
    dailyTrainTicketId: ticketInfo.value.id,
    date: ticketInfo.value.date,
    trainCode: ticketInfo.value.trainCode,
    start: ticketInfo.value.start,
    end: ticketInfo.value.end,
    tickets: confirmInfo.value.passengers.map((passenger, index) => ({
      passengerId: passenger.passengerId,
      passengerName: passenger.passengerName,
      passengerType: passenger.passengerType,
      passengerIdCard: passenger.passengerIdCard,
      seatTypeCode: passenger.seatTypeCode,
      seat: selectedSeats.value[index] || null
    }))
  };
  // 打印最终的订单信息
  console.log('最终订单信息:', confirmData)

  submitOrder(confirmData)
}

// 提交订单
const submitOrder = async (confirmDate) => {
  loading.value = true
  try {
    const res = await confirmOrder(confirmDate)
    if (res.code === 200) {
      // 清除store中的数据
      ticketStore.clearTicketInfo()
      ElMessage.success('订单提交成功！')
      await router.push({path: 'ticket'})
    } else {
      // 场景：http 返回头的状态码是 200，但是返回体中的 code 字段值不是 200
      ElMessage.error(res.msg || '订单提交失败')
    }
  } catch (error) {
    ElMessage.error(error.response.data.msg || '订单提交失败')
  } finally {
    loading.value = false
    confirmLoading.value = false
    dialogVisible.value = false
  }
}
</script>

<template>
  <div class="confirm-container">
    <el-card class="ticket-card">
      <!-- 车次信息 -->
      <template #header>
        <div class="card-header">
          <el-tag size="large" type="primary">{{ ticketInfo.trainCode }}</el-tag>
          <span class="date">{{ ticketInfo.date }}</span>
        </div>
      </template>

      <!-- 行程信息 -->
      <div class="journey-info">
        <div class="station-info start">
          <div class="time">{{ ticketInfo.startTime }}</div>
          <div class="station">{{ ticketInfo.start }}</div>
          <div class="date">{{ ticketInfo.date }}</div>
        </div>
        <div class="journey-duration">
          <el-icon class="arrow-icon"><ArrowRight /></el-icon>
          <div class="duration">
            {{ calculateDuration(ticketInfo.startTime, ticketInfo.endTime) }}
          </div>
        </div>
        <div class="station-info end">
          <div class="time">{{ ticketInfo.endTime }}</div>
          <div class="station">{{ ticketInfo.end }}</div>
          <div class="date">{{ isNextDay(ticketInfo.startTime, ticketInfo.endTime) ? '次日到达' : '当日到达' }}</div>
        </div>
      </div>

      <!-- 座位选择 -->
      <div class="seat-selection">
        <h3>选择座位类型</h3>
        <div class="seat-options">
          <div
            v-for="option in seatOptions"
            :key="option.value"
            class="seat-option-card"
            v-show="option.count !== -1"
            :class="{ 'selected': selectedSeat === option.value }"
            @click="option.count > 0 && (selectedSeat = option.value)"
          >
            <div class="seat-type">{{ option.label }}</div>
            <div class="seat-price">￥{{ option.price }}</div>
            <el-tag
              :type="option.count > 0 ? 'success' : 'info'"
              size="small"
            >
              余票: {{ option.count }}
            </el-tag>
          </div>
        </div>
      </div>

      <!-- 乘客信息 -->
      <div class="passenger-info">
        <h3>选择乘车人</h3>
        <div v-loading="passengerLoading">
          <el-checkbox-group
            v-model="selectedPassengers"
            class="passenger-list"
            @change="handlePassengerChange"
          >
            <el-checkbox
              v-for="passenger in passengerList"
              :key="passenger.id"
              :value="passenger.id"
              class="passenger-item"
            >
              <div class="passenger-detail">
                <div class="name">{{ passenger.name }}</div>
                <div class="id-card">{{ passenger.idCard }}</div>
                <div class="passenger-type">
                  {{ typeOptions.find(o => o.value === passenger.type)?.label }}
                </div>
              </div>
            </el-checkbox>
          </el-checkbox-group>

          <div v-if="passengerList.length === 0" class="no-data">
            <el-empty description="暂无乘车人信息" />
            <el-button type="primary" @click="router.push('/passenger')">
              去添加乘车人
            </el-button>
          </div>
        </div>
      </div>

      <!-- 提交按钮 -->
      <div class="submit-section">
        <div class="price-summary" v-if="selectedSeat">
          <span class="label">{{ seatOptions.find(o => o.value === selectedSeat)?.label }}每张：</span>
          <span class="total-price">￥{{ seatOptions.find(o => o.value === selectedSeat)?.price }}</span>
        </div>
        <el-button
          type="primary"
          :loading="loading"
          @click="handleSubmit"
          size="large"
        >
          提交订单
        </el-button>
      </div>
    </el-card>

    <!-- 确认弹出层 -->
    <el-dialog
      v-model="dialogVisible"
      title="确认订单信息"
      width="600px"
      :close-on-click-modal="false"
      @close="handleCancel"
    >
      <div class="confirm-info">
        <div class="info-section">
          <h4>乘车人信息</h4>
          <el-table :data="confirmInfo.passengers" border>
            <el-table-column prop="passengerName" label="姓名"/>
            <el-table-column label="乘客类型">
              <template #default="{ row }">
                {{ typeOptions.find(o => o.value === row.passengerType)?.label }}
              </template>
            </el-table-column>
            <el-table-column label="座位类型">
              <template #default="{ row }">
                {{ seatOptions.find(o => o.value === row.seatTypeCode)?.label }}
              </template>
            </el-table-column>
          </el-table>
        </div>

        <div v-if="confirmInfo.chooseSeatType > 0" class="info-section">
          <h4>座位选择</h4>
          <div class="seat-info">
            <el-alert
              :title="`您可以选择${confirmInfo.passengers.length}个座位【靠窗：A/F、过道：C/D】`"
              type="info"
              show-icon
              :closable="false"
            />

            <!-- 座位图 -->
            <div v-if="confirmInfo.seats" class="seat-map">
              <!-- 根据乘客数量显示1-2排 -->
              <div
                v-for="row in (confirmInfo.passengers.length > 1 ? 2 : 1)"
                :key="row"
                class="seat-row"
              >
                <div class="row-label">{{ row }}排</div>
                <div class="seats">
                  <el-button
                    v-for="col in confirmInfo.seats[row - 1]"
                    :key="col.col"
                    :type="col.selected ? 'primary' : 'default'"
                    class="seat-button"
                    @click="handleSeatClick(row, col.col)"
                  >
                    {{ col.col }}
                  </el-button>
                </div>
              </div>
            </div>

            <!-- 已选座位展示 -->
            <div class="selected-seats" v-if="selectedSeats.length > 0">
              <div class="label">已选座位：</div>
              <el-tag
                v-for="seat in selectedSeats"
                :key="seat"
                class="seat-tag"
                closable
                @close="() => handleSeatClick(Number(seat.slice(-1)), seat.slice(0, -1))"
              >
                {{ seat }}
              </el-tag>
            </div>
          </div>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleCancel">取消</el-button>
          <el-button
            type="primary"
            @click="handleConfirm"
          >
            确认
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.confirm-container {
  padding: 12px;
  min-height: calc(100vh - 60px);
}

.ticket-card {
  max-width: 800px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 16px;
}

.date {
  color: #909399;
  font-size: 14px;
}

.seat-selection {
  margin-top: 20px;
}

.seat-selection h3 {
  margin-bottom: 15px;
  color: #303133;
  font-size: 16px;
}

.seat-options {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  gap: 12px;
}

.seat-option-card {
  padding: 12px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  flex-direction: column;
  gap: 8px;
  align-items: center;
  background-color: #fff;
}

.seat-option-card:hover {
  border-color: #409EFF;
}

.seat-option-card.selected {
  border-color: #409EFF;
  background-color: #ecf5ff;
}

.seat-option-card[disabled] {
  cursor: not-allowed;
  opacity: 0.6;
}

.seat-type {
  font-weight: bold;
  color: #303133;
}

.seat-price {
  color: #f56c6c;
  font-size: 18px;
  font-weight: bold;
}

.passenger-info {
  margin-top: 20px;
}

.passenger-info h3 {
  margin-bottom: 15px;
}

.submit-section {
  margin-top: 20px;
  padding-top: 15px;
  border-top: 1px solid #ebeef5;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 20px;
}

.price-summary {
  font-size: 14px;
}

.price-summary .label {
  color: #606266;
}

.price-summary .total-price {
  color: #f56c6c;
  font-size: 20px;
  font-weight: bold;
}

:deep(.el-steps) {
  margin: 20px 0;
}

.journey-info {
  margin: 15px 0;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 8px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.station-info {
  text-align: center;
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.station-info.end {
  align-items: flex-end;
}

.station-info .time {
  font-size: 16px;
  color: #606266;
}

.station-info .station {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin: 8px 0;
}

.station-info .date {
  font-size: 14px;
  color: #909399;
}

.journey-duration {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 0 20px;
}

.arrow-icon {
  color: #409EFF;
  font-size: 24px;
  margin-bottom: 8px;
}

.duration {
  font-size: 14px;
  color: #909399;
  white-space: nowrap;
}

.passenger-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.passenger-item {
  width: 100%;
  padding: 12px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  transition: all 0.3s;
}

.passenger-item:hover {
  border-color: #409EFF;
}

.passenger-item.is-checked {
  border-color: #409EFF;
  background-color: #ecf5ff;
}

.passenger-detail {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-left: 8px;
}

.name {
  font-weight: bold;
  min-width: 60px;
}

.id-card {
  color: #606266;
  min-width: 160px;
}

.passenger-type {
  color: #606266;
  background-color: #f4f4f5;
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 13px;
}

.no-data {
  text-align: center;
  padding: 20px;
}

.no-data .el-button {
  margin-top: 16px;
}

.confirm-info {
  padding: 10px;
}

.info-section {
  margin-bottom: 20px;
}

.info-section h4 {
  margin-bottom: 15px;
  color: #303133;
  font-size: 16px;
}

.seat-info {
  margin-top: 10px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.seat-map {
  margin-top: 20px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.seat-row {
  display: flex;
  align-items: center;
  gap: 16px;
}

.row-label {
  width: 40px;
  color: #606266;
}

.seats {
  display: flex;
  gap: 8px;
}

.seat-button {
  width: 40px;
  height: 40px;
  padding: 0;
}

.selected-seats {
  margin-top: 20px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.selected-seats .label {
  color: #606266;
}

.seat-tag {
  margin-right: 8px;
}
</style>
