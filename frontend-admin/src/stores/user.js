import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const email = ref(localStorage.getItem('userEmail') || '')
  const token = ref(localStorage.getItem('userToken') || '')

  const setUserInfo = (userInfo) => {
    email.value = userInfo.email
    token.value = userInfo.token

    localStorage.setItem('userEmail', userInfo.email)
    localStorage.setItem('userToken', userInfo.token)
  }

  const clearUserInfo = () => {
    email.value = ''
    token.value = ''

    localStorage.removeItem('userEmail')
    localStorage.removeItem('userToken')
  }

  return {
    email,
    token,
    setUserInfo,
    clearUserInfo
  }
})
