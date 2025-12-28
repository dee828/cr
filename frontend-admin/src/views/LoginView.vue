<script setup lang="ts">
import { reactive, ref, computed } from 'vue'
import { User, Lock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

import { login } from '@/api/auth'

const formRef = ref()
const router = useRouter()
const userStore = useUserStore()

// 登录表单数据
const loginForm = reactive({
  email: 'user@example.com',
  password: ''
})

// 表单验证规则
const rules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少 6 位', trigger: 'blur' }
  ]
}

// 根据密码输入框的变化，动态计算按钮是否可以被点击
const isLoginDisabled = computed(() => {
  return !loginForm.password || loginForm.password.length < 6;
})

// 登录处理函数（暂时只打印）
const handleLogin = () => {
  // 获取校验通过之后的邮箱和密码
  if (!formRef.value) return
  formRef.value.validate()
  console.log('登录表单数据：', loginForm)

  // 发送请求对接自己写的后端登录接口（AuthController 的 login 方法）
  login(loginForm.email, loginForm.password)
    .then(res => {
      ElMessage.success('登录成功')
      router.push('/dashboard')
      userStore.setUserInfo(res.data)
    })
    .catch(error => {
      ElMessage.error(error.response?.data?.msg || '登录失败')
    })

  // 把登录成功返回的 token 记录下来（Pinia 和 Local storage）
  // 以后每一次请求其他后端接口都带上这个 token
}
</script>

<template>
  <div class="login-container">
    <div class="login-box">
      <h1 class="login-title">Admin 用户登录</h1>

      <el-form ref="formRef" :model="loginForm" :rules="rules" label-width="0">
        <el-form-item prop="email">
          <el-input
            v-model="loginForm.email"
            placeholder="请输入邮箱"
            size="large"
            clearable
          >
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            show-password
            clearable
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            style="width: 100%"
            :disabled="isLoginDisabled"
            @click="handleLogin"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
  width: 400px;
  padding: 40px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.login-title {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
  font-size: 24px;
}
</style>
