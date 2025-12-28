<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { Plus, Refresh } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { saveJob, listJob, deleteJob, pauseJob, resumeJob, rescheduleJob, runJob } from '@/api/scheduler/job.js'

// 定时任务列表数据
const jobList = ref([])

// 控制新增对话框的显示
const dialogVisible = ref(false)

// 是否是编辑模式
const isEdit = ref(false)

// 新增定时任务表单数据
const jobForm = reactive({
  name: '',
  group: 'default',
  cronExpression: '',
  description: ''
})

// 表单规则
const rules = {
  name: [
    { required: true, message: '请输入任务类名', trigger: 'blur' }
  ],
  group: [
    { required: true, message: '请输入任务分组', trigger: 'blur' }
  ],
  cronExpression: [
    { required: true, message: '请输入CRON表达式', trigger: 'blur' }
  ],
  description: [
    { required: false, message: '请输入任务描述', trigger: 'blur' }
  ]
}

const formRef = ref(null)
const tableLoading = ref(false)

// 获取定时任务列表数据
const getJobList = async () => {
  tableLoading.value = true
  try {
    const res = await listJob()
    if (res.code === 200) {
      jobList.value = res.data
    } else {
      ElMessage.error(res.msg || '获取定时任务列表失败')
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.msg || '获取定时任务列表失败')
  } finally {
    tableLoading.value = false
  }
}

// 在组件挂载时获取数据
onMounted(() => {
  getJobList()
})

// 重置表单
const resetForm = () => {
  isEdit.value = false
  jobForm.name = ''
  jobForm.group = 'default'
  jobForm.cronExpression = ''
  jobForm.description = ''
}

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return

  let res;
  try {
    await formRef.value.validate()
    if (isEdit.value) {
      res = await rescheduleJob(jobForm)
    } else {
      res = await saveJob(jobForm)
    }
    if (res.code === 200) {
      ElMessage.success(isEdit.value ? '编辑成功' : '添加成功')
      dialogVisible.value = false
      resetForm()
      // 刷新定时任务列表
      await getJobList()
    } else {
      // 场景：http 返回头的状态码是 200，但是返回体中的 code 字段值不是 200
      ElMessage.error(res.msg || (jobForm.id ? '编辑定时任务失败' : '添加定时任务失败'))
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.msg || (isEdit.value ? '编辑失败' : '添加失败'))
  }
}

// 删除任务
const handleDelete = (row) => {
  ElMessageBox.confirm(
    '确定要删除该定时任务吗？',
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteJob({
        name: row.name,
        group: row.group
      })
      ElMessage.success('删除成功')
      await getJobList()
    } catch (error) {
      ElMessage.error(error.response?.data?.msg || '删除失败')
    }
  })
}

// 暂停任务
const handlePause = async (row) => {
  try {
    const res = await pauseJob({
      name: row.name,
      group: row.group
    })
    if (res.code === 200) {
      ElMessage.success('暂停成功')
      setTimeout(async () => {
        await getJobList()
      }, 800)
    } else {
      ElMessage.error(res.msg || '暂停失败')
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.msg || '暂停失败')
  }
}

// 恢复任务
const handleResume = async (row) => {
  try {
    const res = await resumeJob({
      name: row.name,
      group: row.group
    })
    if (res.code === 200) {
      ElMessage.success('恢复成功')
      setTimeout(async () => {
        await getJobList()
      }, 800)
    } else {
      ElMessage.error(res.msg || '恢复失败')
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.msg || '恢复失败')
  }
}

// 立即单独执行指定任务（计划外、一次性、手工补偿）
const handleRun = async (row) => {
  try {
    await ElMessageBox.confirm(
      '确定要立即执行该任务吗？',
      '执行确认',
      {
        confirmButtonText: '确定执行',
        cancelButtonText: '取消',
        type: 'warning',
        dangerouslyUseHTMLString: true,
        message: `
          <p>请注意：</p>
          <ol style="margin: 10px 0; padding-left: 20px; color: #666;">
            <li>这是一次性的补偿操作</li>
            <li>任务将立即执行一次</li>
            <li>不影响原有的定时调度计划</li>
          </ol>
          <p style="color: #E6A23C;">任务信息：${row.name}</p>
        `
      }
    )

    await runJob({
      name: row.name,
      group: row.group
    })
    ElMessage.success('任务已触发执行')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('执行失败')
    }
  }
}

const handleRefresh = () => {
  getJobList()
}

const dialogTitle = computed(() => isEdit.value ? '编辑定时任务' : '新增定时任务')

// 编辑任务
const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(jobForm, {
    name: row.name,
    group: row.group,
    cronExpression: row.cronExpression,
    description: row.description
  })
  dialogVisible.value = true
}

// 设置 CRON 表达式
const setCronExpression = (expression) => {
  jobForm.cronExpression = expression
  ElMessage.success('已设置 CRON 表达式')
}
</script>

<template>
  <div class="job-container">
    <!-- 顶部按钮 -->
    <div class="top-tools">
      <div class="left">
        <el-button type="primary" :icon="Plus" @click="dialogVisible = true">新增定时任务</el-button>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </div>
    </div>

    <!-- 定时任务列表 -->
    <el-table
      :data="jobList"
      v-loading="tableLoading"
      style="width: 100%"
      border
    >
      <el-table-column prop="name" label="任务类名" width="300" show-overflow-tooltip />
      <el-table-column prop="group" label="任务分组" width="120" />
      <el-table-column prop="cronExpression" label="CRON表达式" />
      <el-table-column prop="description" label="任务描述" />
      <el-table-column prop="state" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.state === 'NORMAL' ? 'success' : 'warning'">
            {{ row.state === 'NORMAL' ? '运行中' : '已暂停' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="nextFireTime" label="下次执行时间" width="180">
        <template #default="{ row }">
          {{ row.nextFireTime ? new Date(row.nextFireTime).toLocaleString() : '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="preFireTime" label="上次执行时间" width="180">
        <template #default="{ row }">
          {{ row.preFireTime ? new Date(row.preFireTime).toLocaleString() : '-' }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button
            v-if="row.state === 'NORMAL'"
            type="warning"
            link
            @click="handlePause(row)"
          >暂停</el-button>
          <el-button
            v-else
            type="success"
            link
            @click="handleResume(row)"
          >恢复</el-button>
          <el-button type="primary" link @click="handleRun(row)">执行</el-button>
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增定时任务对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="520px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      destroy-on-close
      @closed="resetForm"
    >
      <el-form
        ref="formRef"
        :model="jobForm"
        :rules="rules"
        label-width="120px"
        status-icon
      >
        <el-form-item label="任务类名" prop="name">
          <el-input
            v-model="jobForm.name"
            placeholder="请输入任务类名（包含完整包名）"
            clearable
            :disabled="isEdit"
          />
        </el-form-item>
        <el-form-item label="任务分组" prop="group">
          <el-input
            v-model="jobForm.group"
            placeholder="请输入任务分组"
            clearable
            :disabled="isEdit"
          />
        </el-form-item>
        <el-form-item label="CRON表达式" prop="cronExpression">
          <el-tooltip
            effect="light"
            placement="right"
            :show-after="500"
            :width="400"
          >
            <template #content>
              <div style="text-align: left">
                <p><strong>常见CRON表达式示例：</strong></p>
                <p class="cron-example" @click="setCronExpression('*/5 * * * * ?')">每隔5秒执行: */5 * * * * ?</p>
                <p class="cron-example" @click="setCronExpression('0 */1 * * * ?')">每隔1分钟执行: 0 */1 * * * ?</p>
                <p class="cron-example" @click="setCronExpression('0 0 23 * * ?')">每天23点执行: 0 0 23 * * ?</p>
                <p class="cron-example" @click="setCronExpression('0 0 1 * * ?')">每天凌晨1点执行: 0 0 1 * * ?</p>
                <p class="cron-example" @click="setCronExpression('0 0 1 1 * ?')">每月1号凌晨1点执行: 0 0 1 1 * ?</p>
                <p class="cron-example" @click="setCronExpression('0 0 23 L * ?')">每月最后一天23点执行: 0 0 23 L * ?</p>
                <p class="cron-example" @click="setCronExpression('0 0 1 ? * L')">每周星期天凌晨1点执行: 0 0 1 ? * L</p>
                <hr style="margin: 8px 0"/>
                <p><strong>表达式格式：</strong></p>
                <p>秒 分 时 日 月 周</p>
                <p><strong>取值范围：</strong></p>
                <p>秒：0-59</p>
                <p>分：0-59</p>
                <p>时：0-23</p>
                <p>日：1-31</p>
                <p>月：1-12</p>
                <p>周：1-7（1=周日）</p>
              </div>
            </template>
            <el-input
              v-model="jobForm.cronExpression"
              placeholder="请输入CRON表达式（鼠标悬停查看示例）"
              clearable
            />
          </el-tooltip>
        </el-form-item>
        <el-form-item label="任务描述" prop="description">
          <el-input
            v-model="jobForm.description"
            placeholder="请输入任务描述"
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
.job-container {
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

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.cron-example {
  cursor: pointer;
  padding: 4px 8px;
  margin: 2px 0;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.cron-example:hover {
  background-color: #f5f7fa;
}
</style>
