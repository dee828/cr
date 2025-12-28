import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useTicketStore = defineStore('ticket', () => {
  // 存储预订车票的信息
  const ticketInfo = ref(null)

  // 设置车票信息
  const setTicketInfo = (info) => {
    ticketInfo.value = info
  }

  // 清除车票信息
  const clearTicketInfo = () => {
    ticketInfo.value = null
  }

  return {
    ticketInfo,
    setTicketInfo,
    clearTicketInfo
  }
})
