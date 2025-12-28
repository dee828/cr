import http from '../http.js'

const MODULE_PREFIX = '/business'

/**
 * 获取每日车厢座位列表
 * @param {Object} params 查询参数
 * @param {number} params.page 当前页码
 * @param {number} params.size 每页显示条数
 * @returns {Promise}
 */
export function listDailyTrainSeat(params = {}) {
  const { page = 1, size = 10, keyword } = params
  return http.get(MODULE_PREFIX + '/admin/daily-train-seat/list', {
    params: {
      page,
      size,
      keyword
    }
  })
}

export function saveDailyTrainSeat(data) {
  return http.post(MODULE_PREFIX + '/admin/daily-train-seat/save', data)
}

export function deleteDailyTrainSeat(id) {
  return http.delete(MODULE_PREFIX + '/admin/daily-train-seat/' + id)
}
