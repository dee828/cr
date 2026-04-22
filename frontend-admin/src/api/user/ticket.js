import http from '../http.js'

const MODULE_PREFIX = '/user'

/**
 * 获取车票记录列表
 * @param {Object} params 查询参数
 * @param {number} params.page 当前页码
 * @param {number} params.size 每页显示条数
 * @returns {Promise}
 */
export function listTicket(params = {}) {
  const { page = 1, size = 10, keyword } = params
  return http.get(MODULE_PREFIX + '/admin/ticket/list', {
    params: {
      page,
      size,
      keyword
    }
  })
}

export function saveTicket(data) {
  return http.post(MODULE_PREFIX + '/admin/ticket/save', data)
}

export function deleteTicket(id) {
  return http.delete(MODULE_PREFIX + '/admin/ticket/' + id)
}
