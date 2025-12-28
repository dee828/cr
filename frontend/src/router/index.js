import { createWebHistory, createRouter } from 'vue-router'
import AppLayout from '@/layout/AppLayout.vue'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/login',
    component: () => import('@/views/LoginView.vue'),
    meta: {
      title: '登录',
      requiresAuth: false  // 登录页不需要验证
    }
  },
  {
    path: '/',
    component: AppLayout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '仪表盘' }
      },
      {
        path: 'empty',
        name: 'Empty',
        component: () => import('@/views/Empty.vue'),
        meta: { title: '空白页面' }
      },
      {
        path: 'passenger',
        name: 'Passenger',
        component: () => import('@/views/user/Passenger.vue'),
        meta: { title: '乘车人' }
      },
      {
        path: 'daily-train-ticket',
        name: 'DailyTrainTicket',
        component: () => import('@/views/business/DailyTrainTicket.vue'),
        meta: { title: '余票信息' }
      },
      {
        path: 'confirm',
        name: 'Confirm',
        component: () => import('../views/business/Confirm.vue'),
        meta: { title: '确认订单' }
      }
    ]
  }
]

export const router = createRouter({
  history: createWebHistory(),
  routes,
})

// 全局前置守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()

  // 如果页面需要认证（默认都需要认证，除非明确标记不需要）
  if (to.meta.requiresAuth !== false) {
    // 检查是否有 token
    if (!userStore.token) {
      // 如果没有 token，重定向到登录页
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
      return
    }
  }

  // 如果已登录且要访问登录页，重定向到首页
  if (to.path === '/login' && userStore.token) {
    next({ path: '/dashboard' })
    return
  }

  next()
})
