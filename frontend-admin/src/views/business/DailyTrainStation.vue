<script setup>
import {ref, reactive, onMounted, computed} from 'vue'
import {Plus, Refresh} from '@element-plus/icons-vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {saveDailyTrainStation, deleteDailyTrainStation, listDailyTrainStation} from '@/api/business/dailyTrainStation.js'

// 每日火车车站列表数据
const dailyTrainStationList = ref([])

// 控制对话框的显示
const dialogVisible = ref(false)

// 每日火车车站表单数据
const dailyTrainStationForm = reactive({
  id: null,
  date: null,
  trainCode: null,
  index: 0,
  name: null,
  namePinyin: null,
  inTime: null,
  outTime: null,
  stopTime: null,
  km: 0.0,
})

// 表单规则
const rules = {
  date: [
    {required: true, message: '请输入日期', trigger: 'blur'},
  ],
  trainCode: [
    {required: true, message: '请输入车次编号', trigger: 'blur'},
  ],
  index: [
    {required: true, message: '请输入站序', trigger: 'blur'},
  ],
  name: [
    {required: true, message: '请输入站名', trigger: 'blur'},
  ],
  namePinyin: [
    {required: true, message: '请输入站名拼音', trigger: 'blur'},
  ],
  inTime: [
    {required: false, message: '请输入进站时间', trigger: 'blur'},
  ],
  outTime: [
    {required: false, message: '请输入出站时间', trigger: 'blur'},
  ],
  stopTime: [
    {required: false, message: '请输入停站时长', trigger: 'blur'},
  ],
  km: [
    {required: true, message: '请输入里程 km', trigger: 'blur'},
  ],
}

const formRef = ref(null)

const handleAdd = () => {
  dialogVisible.value = true
}

const resetForm = () => {
  dailyTrainStationForm.id = null
  dailyTrainStationForm.date = null
  dailyTrainStationForm.trainCode = null
  dailyTrainStationForm.index = 0
  dailyTrainStationForm.name = null
  dailyTrainStationForm.namePinyin = null
  dailyTrainStationForm.inTime = null
  dailyTrainStationForm.outTime = null
  dailyTrainStationForm.stopTime = null
  dailyTrainStationForm.km = 0.0
}

const submitForm = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    const res = await saveDailyTrainStation(dailyTrainStationForm)
    if (res.code === 200) {
      ElMessage.success(dailyTrainStationForm.id ? '编辑成功' : '添加成功')
      dialogVisible.value = false
      resetForm()
      // 刷新乘客列表
      pagination.page = 1
      await getDailyTrainStationList()
    } else {
      // 场景：http 返回头的状态码是 200，但是返回体中的 code 字段值不是 200
      ElMessage.error(res.msg || (dailyTrainStationForm.id ? '编辑每日火车车站失败' : '添加每日火车车站失败'))
    }
  } catch (error) {
    ElMessage.error(error.response.data.msg || (dailyTrainStationForm.id ? '编辑每日火车车站失败' : '添加每日火车车站失败'))
  }
}

const handleEdit = (row) => {
  Object.assign(dailyTrainStationForm, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm(
    '确定要删除每日火车车站 ' + row.id + ' 吗？',
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const res = await deleteDailyTrainStation(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        // 如果当前页只有一条数据且不是第一页，删除后自动跳转到上一页
        if (dailyTrainStationList.value.length === 1 && pagination.page > 1) {
          pagination.page--
        }
        await getDailyTrainStationList()
      } else {
        ElMessage.error(res.msg || '删除失败')
      }
    } catch (error) {
      console.error('删除每日火车车站失败:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {
    // 取消删除，不做任何操作
    console.log('取消删除，不做任何操作')
  })
}

const dialogTitle = computed(() => {
  return dailyTrainStationForm.id ? '编辑每日火车车站' : '新增每日火车车站'
})

const tableLoading = ref(false)

// 获取每日火车车站列表数据
const getDailyTrainStationList = async () => {
  tableLoading.value = true
  try {
    const res = await listDailyTrainStation({
      ...pagination,
      keyword: searchForm.keyword
    })
    if (res.code === 200) {
      dailyTrainStationList.value = res.data.list
      pagination.total = res.data.total
    } else {
      ElMessage.error(res.msg || '获取每日火车车站列表失败')
    }
  } catch (error) {
    console.error('获取每日火车车站列表失败:', error)
    ElMessage.error('获取每日火车车站列表失败')
  } finally {
    tableLoading.value = false
  }
}

// 在组件挂载时获取数据
onMounted(() => {
  getDailyTrainStationList()
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
  await getDailyTrainStationList()
}

const handleSizeChange = async (val) => {
  pagination.size = val
  pagination.page = 1
  await getDailyTrainStationList()
}

const handleRefresh = () => {
  getDailyTrainStationList()
}

// 搜索表单
const searchForm = reactive({
  keyword: ''  // 搜索关键字
})

// 处理搜索
const handleSearch = () => {
  pagination.page = 1  // 重置到第一页
  getDailyTrainStationList()
}

// 重置搜索
const handleReset = () => {
  searchForm.keyword = ''
  handleSearch()
}
</script>

<template>
  <div class="dailyTrainStation-container">
    <!-- 顶部按钮 -->
    <div class="top-tools">
      <div class="left">
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增每日火车车站</el-button>
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

    <!-- 每日火车车站列表 -->
    <el-table
      :data="dailyTrainStationList"
      v-loading="tableLoading"
      style="width: 100%"
      border
    >
      <el-table-column prop="date" label="日期"/>
      <el-table-column prop="trainCode" label="车次编号"/>
      <el-table-column prop="index" label="站序"/>
      <el-table-column prop="name" label="站名"/>
      <el-table-column prop="namePinyin" label="站名拼音"/>
      <el-table-column prop="inTime" label="进站时间"/>
      <el-table-column prop="outTime" label="出站时间"/>
      <el-table-column prop="stopTime" label="停站时长"/>
      <el-table-column prop="km" label="里程 km"/>
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

    <!-- 每日火车车站对话框 -->
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
        :model="dailyTrainStationForm"
        :rules="rules"
        label-width="100px"
        status-icon
      >
        <el-form-item label="日期" prop="date">
          <el-date-picker
            v-model="dailyTrainStationForm.date"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请输入日期"
          />
        </el-form-item>
        <el-form-item label="车次编号" prop="trainCode">
          <el-input
            v-model="dailyTrainStationForm.trainCode"
            placeholder="请输入车次编号"
            clearable
          />
        </el-form-item>
        <el-form-item label="站序" prop="index">
          <el-input-number v-model="dailyTrainStationForm.index" />
        </el-form-item>
        <el-form-item label="站名" prop="name">
          <el-input
            v-model="dailyTrainStationForm.name"
            placeholder="请输入站名"
            clearable
          />
        </el-form-item>
        <el-form-item label="站名拼音" prop="namePinyin">
          <el-input
            v-model="dailyTrainStationForm.namePinyin"
            placeholder="请输入站名拼音"
            clearable
          />
        </el-form-item>
        <el-form-item label="进站时间" prop="inTime">
          <el-time-picker
            v-model="dailyTrainStationForm.inTime"
            placeholder="请输入进站时间"
            value-format="HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="出站时间" prop="outTime">
          <el-time-picker
            v-model="dailyTrainStationForm.outTime"
            placeholder="请输入出站时间"
            value-format="HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="停站时长" prop="stopTime">
          <el-time-picker
            v-model="dailyTrainStationForm.stopTime"
            placeholder="请输入停站时长"
            value-format="HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="里程 km" prop="km">
          <el-input-number v-model="dailyTrainStationForm.km" :precision="2" />
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
.dailyTrainStation-container {
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
