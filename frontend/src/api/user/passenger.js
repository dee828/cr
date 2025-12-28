import http from '../http.js'

const MODULE_PREFIX = '/user'

/**
 * 获取乘车人/乘客列表
 * @param {Object} params 查询参数
 * @param {number} params.page 当前页码
 * @param {number} params.size 每页显示条数
 * @returns {Promise}
 */
export function listPassenger(params = {}) {
  const { page = 1, size = 10, keyword } = params
  return http.get(MODULE_PREFIX + '/passenger/list', {
    params: {
      page,
      size,
      keyword
    }
  })
}

export function savePassenger(data) {
  return http.post(MODULE_PREFIX + '/passenger/save', data)
}

export function deletePassenger(id) {
  return http.delete(MODULE_PREFIX + '/passenger/' + id)
}
