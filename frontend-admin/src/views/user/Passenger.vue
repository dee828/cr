<script setup>
import {ref, reactive, onMounted, computed} from 'vue'
import {Plus, Refresh} from '@element-plus/icons-vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {savePassenger, deletePassenger, listPassenger} from '@/api/user/passenger.js'

// 乘车人/乘客列表数据
const passengerList = ref([])

// 控制对话框的显示
const dialogVisible = ref(false)

// 乘车人/乘客表单数据
const passengerForm = reactive({
  id: '',
  userId: '',
  name: '',
  idCard: '',
  mobile: '',
  type: null,
})

// 表单规则
const rules = {
  name: [
    {required: true, message: '请输入姓名', trigger: 'blur'},
  ],
  idCard: [
    {required: true, message: '请输入身份证号', trigger: 'blur'},
    {pattern: /^\d{18}$/, message: '身份证号必须为18位数字', trigger: 'blur'}
  ],
  mobile: [
    {required: true, message: '请输入手机号', trigger: 'blur'},
    {pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur'}
  ],
  type: [
    {required: true, message: '请输入乘客类型', trigger: 'blur'},
  ],
}

const formRef = ref(null)

const handleAdd = () => {
  dialogVisible.value = true
}

const resetForm = () => {
  passengerForm.id = ''
  passengerForm.userId = ''
  passengerForm.name = ''
  passengerForm.idCard = ''
  passengerForm.mobile = ''
  passengerForm.type = null
}

const submitForm = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    const res = await savePassenger(passengerForm)
    if (res.code === 200) {
      ElMessage.success(passengerForm.id ? '编辑成功' : '添加成功')
      dialogVisible.value = false
      resetForm()
      // 刷新乘客列表
      pagination.page = 1
      await getPassengerList()
    } else {
      // 场景：http 返回头的状态码是 200，但是返回体中的 code 字段值不是 200
      ElMessage.error(res.msg || (passengerForm.id ? '编辑乘车人/乘客失败' : '添加乘车人/乘客失败'))
    }
  } catch (error) {
    ElMessage.error(error.response.data.msg || (passengerForm.id ? '编辑乘车人/乘客失败' : '添加乘车人/乘客失败'))
  }
}

const handleEdit = (row) => {
  Object.assign(passengerForm, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm(
    '确定要删除乘车人/乘客 ' + row.id + ' 吗？',
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const res = await deletePassenger(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        // 如果当前页只有一条数据且不是第一页，删除后自动跳转到上一页
        if (passengerList.value.length === 1 && pagination.page > 1) {
          pagination.page--
        }
        await getPassengerList()
      } else {
        ElMessage.error(res.msg || '删除失败')
      }
    } catch (error) {
      console.error('删除乘车人/乘客失败:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {
    // 取消删除，不做任何操作
    console.log('取消删除，不做任何操作')
  })
}

const dialogTitle = computed(() => {
  return passengerForm.id ? '编辑乘车人/乘客' : '新增乘车人/乘客'
})

const tableLoading = ref(false)

// 获取乘车人/乘客列表数据
const getPassengerList = async () => {
  tableLoading.value = true
  try {
    const res = await listPassenger({
      ...pagination,
      keyword: searchForm.keyword
    })
    if (res.code === 200) {
      passengerList.value = res.data.list
      pagination.total = res.data.total
    } else {
      ElMessage.error(res.msg || '获取乘车人/乘客列表失败')
    }
  } catch (error) {
    console.error('获取乘车人/乘客列表失败:', error)
    ElMessage.error('获取乘车人/乘客列表失败')
  } finally {
    tableLoading.value = false
  }
}

// 在组件挂载时获取数据
onMounted(() => {
  getPassengerList()
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
  await getPassengerList()
}

const handleSizeChange = async (val) => {
  pagination.size = val
  pagination.page = 1
  await getPassengerList()
}

const handleRefresh = () => {
  getPassengerList()
}

// 乘客类型选项
const typeOptions = [
  { label: '成人', value: '1' },
  { label: '儿童', value: '2' },
  { label: '学生', value: '3' }
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
  getPassengerList()
}

// 重置搜索
const handleReset = () => {
  searchForm.keyword = ''
  handleSearch()
}
</script>

<template>
  <div class="passenger-container">
    <!-- 顶部按钮 -->
    <div class="top-tools">
      <div class="left">
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增乘车人/乘客</el-button>
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

    <!-- 乘车人/乘客列表 -->
    <el-table
      :data="passengerList"
      v-loading="tableLoading"
      style="width: 100%"
      border
    >
      <el-table-column prop="userId" label="会员id"/>
      <el-table-column prop="name" label="姓名"/>
      <el-table-column prop="idCard" label="身份证号"/>
      <el-table-column prop="mobile" label="手机号"/>
      <el-table-column prop="type" label="乘客类型">
        <template #default="{ row }">
          {{ getOptionLabel('type', row.type) }}
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

    <!-- 乘车人/乘客对话框 -->
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
        :model="passengerForm"
        :rules="rules"
        label-width="100px"
        status-icon
      >
        <el-form-item label="会员id" prop="userId">
          <el-input
            v-model="passengerForm.userId"
            placeholder="请输入会员id"
            clearable
          />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input
            v-model="passengerForm.name"
            placeholder="请输入姓名"
            clearable
          />
        </el-form-item>
        <el-form-item label="身份证号" prop="idCard">
          <el-input
            v-model="passengerForm.idCard"
            placeholder="请输入身份证号"
            clearable
          />
        </el-form-item>
        <el-form-item label="手机号" prop="mobile">
          <el-input
            v-model="passengerForm.mobile"
            placeholder="请输入手机号"
            clearable
          />
        </el-form-item>
        <el-form-item label="乘客类型" prop="type">
          <el-select
            v-model="passengerForm.type"
            style="width: 100%"
            placeholder="请选择乘客类型"
          >
            <el-option
              v-for="option in typeOptions"
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
.passenger-container {
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
