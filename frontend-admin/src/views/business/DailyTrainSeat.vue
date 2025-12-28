<script setup>
import {ref, reactive, onMounted, computed} from 'vue'
import {Plus, Refresh} from '@element-plus/icons-vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {saveDailyTrainSeat, deleteDailyTrainSeat, listDailyTrainSeat} from '@/api/business/dailyTrainSeat.js'

// 每日车厢座位列表数据
const dailyTrainSeatList = ref([])

// 控制对话框的显示
const dialogVisible = ref(false)

// 每日车厢座位表单数据
const dailyTrainSeatForm = reactive({
  id: null,
  date: null,
  trainCode: null,
  carriageIndex: 0,
  row: null,
  seatType: null,
  col: null,
  carriageSeatIndex: 0,
  sell: null,
})

// 表单规则
const rules = {
  date: [
    {required: true, message: '请输入日期', trigger: 'blur'},
  ],
  trainCode: [
    {required: true, message: '请输入车次编号', trigger: 'blur'},
  ],
  carriageIndex: [
    {required: true, message: '请输入厢序', trigger: 'blur'},
  ],
  row: [
    {required: true, message: '请输入排号', trigger: 'blur'},
  ],
  seatType: [
    {required: true, message: '请输入座位类型', trigger: 'blur'},
  ],
  col: [
    {required: true, message: '请输入列号', trigger: 'blur'},
  ],
  carriageSeatIndex: [
    {required: true, message: '请输入同车厢座序', trigger: 'blur'},
  ],
  sell: [
    {required: true, message: '请输入售卖情况', trigger: 'blur'},
  ],
}

const formRef = ref(null)

const handleAdd = () => {
  dialogVisible.value = true
}

const resetForm = () => {
  dailyTrainSeatForm.id = null
  dailyTrainSeatForm.date = null
  dailyTrainSeatForm.trainCode = null
  dailyTrainSeatForm.carriageIndex = 0
  dailyTrainSeatForm.row = null
  dailyTrainSeatForm.seatType = null
  dailyTrainSeatForm.col = null
  dailyTrainSeatForm.carriageSeatIndex = 0
  dailyTrainSeatForm.sell = null
}

const submitForm = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    const res = await saveDailyTrainSeat(dailyTrainSeatForm)
    if (res.code === 200) {
      ElMessage.success(dailyTrainSeatForm.id ? '编辑成功' : '添加成功')
      dialogVisible.value = false
      resetForm()
      // 刷新乘客列表
      pagination.page = 1
      await getDailyTrainSeatList()
    } else {
      // 场景：http 返回头的状态码是 200，但是返回体中的 code 字段值不是 200
      ElMessage.error(res.msg || (dailyTrainSeatForm.id ? '编辑每日车厢座位失败' : '添加每日车厢座位失败'))
    }
  } catch (error) {
    ElMessage.error(error.response.data.msg || (dailyTrainSeatForm.id ? '编辑每日车厢座位失败' : '添加每日车厢座位失败'))
  }
}

const handleEdit = (row) => {
  Object.assign(dailyTrainSeatForm, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm(
    '确定要删除每日车厢座位 ' + row.id + ' 吗？',
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const res = await deleteDailyTrainSeat(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        // 如果当前页只有一条数据且不是第一页，删除后自动跳转到上一页
        if (dailyTrainSeatList.value.length === 1 && pagination.page > 1) {
          pagination.page--
        }
        await getDailyTrainSeatList()
      } else {
        ElMessage.error(res.msg || '删除失败')
      }
    } catch (error) {
      console.error('删除每日车厢座位失败:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {
    // 取消删除，不做任何操作
    console.log('取消删除，不做任何操作')
  })
}

const dialogTitle = computed(() => {
  return dailyTrainSeatForm.id ? '编辑每日车厢座位' : '新增每日车厢座位'
})

const tableLoading = ref(false)

// 获取每日车厢座位列表数据
const getDailyTrainSeatList = async () => {
  tableLoading.value = true
  try {
    const res = await listDailyTrainSeat({
      ...pagination,
      keyword: searchForm.keyword
    })
    if (res.code === 200) {
      dailyTrainSeatList.value = res.data.list
      pagination.total = res.data.total
    } else {
      ElMessage.error(res.msg || '获取每日车厢座位列表失败')
    }
  } catch (error) {
    console.error('获取每日车厢座位列表失败:', error)
    ElMessage.error('获取每日车厢座位列表失败')
  } finally {
    tableLoading.value = false
  }
}

// 在组件挂载时获取数据
onMounted(() => {
  getDailyTrainSeatList()
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
  await getDailyTrainSeatList()
}

const handleSizeChange = async (val) => {
  pagination.size = val
  pagination.page = 1
  await getDailyTrainSeatList()
}

const handleRefresh = () => {
  getDailyTrainSeatList()
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
  getDailyTrainSeatList()
}

// 重置搜索
const handleReset = () => {
  searchForm.keyword = ''
  handleSearch()
}
</script>

<template>
  <div class="dailyTrainSeat-container">
    <!-- 顶部按钮 -->
    <div class="top-tools">
      <div class="left">
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增每日车厢座位</el-button>
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

    <!-- 每日车厢座位列表 -->
    <el-table
      :data="dailyTrainSeatList"
      v-loading="tableLoading"
      style="width: 100%"
      border
    >
      <el-table-column prop="date" label="日期"/>
      <el-table-column prop="trainCode" label="车次编号"/>
      <el-table-column prop="carriageIndex" label="厢序"/>
      <el-table-column prop="row" label="排号"/>
      <el-table-column prop="seatType" label="座位类型">
        <template #default="{ row }">
          {{ getOptionLabel('seatType', row.seatType) }}
        </template>
      </el-table-column>
      <el-table-column prop="col" label="列号"/>
      <el-table-column prop="carriageSeatIndex" label="同车厢座序"/>
      <el-table-column prop="sell" label="售卖情况"/>
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

    <!-- 每日车厢座位对话框 -->
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
        :model="dailyTrainSeatForm"
        :rules="rules"
        label-width="100px"
        status-icon
      >
        <el-form-item label="日期" prop="date">
          <el-date-picker
            v-model="dailyTrainSeatForm.date"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请输入日期"
          />
        </el-form-item>
        <el-form-item label="车次编号" prop="trainCode">
          <el-input
            v-model="dailyTrainSeatForm.trainCode"
            placeholder="请输入车次编号"
            clearable
          />
        </el-form-item>
        <el-form-item label="厢序" prop="carriageIndex">
          <el-input-number v-model="dailyTrainSeatForm.carriageIndex" />
        </el-form-item>
        <el-form-item label="排号" prop="row">
          <el-input
            v-model="dailyTrainSeatForm.row"
            placeholder="请输入排号"
            clearable
          />
        </el-form-item>
        <el-form-item label="座位类型" prop="seatType">
          <el-select
            v-model="dailyTrainSeatForm.seatType"
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
        <el-form-item label="列号" prop="col">
          <el-input
            v-model="dailyTrainSeatForm.col"
            placeholder="请输入列号"
            clearable
          />
        </el-form-item>
        <el-form-item label="同车厢座序" prop="carriageSeatIndex">
          <el-input-number v-model="dailyTrainSeatForm.carriageSeatIndex" />
        </el-form-item>
        <el-form-item label="售卖情况" prop="sell">
          <el-input
            v-model="dailyTrainSeatForm.sell"
            placeholder="请输入售卖情况"
            clearable
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
.dailyTrainSeat-container {
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
