import http from '../http.js'

const MODULE_PREFIX = '/business'

/**
 * 获取火车车站列表
 * @param {Object} params 查询参数
 * @param {number} params.page 当前页码
 * @param {number} params.size 每页显示条数
 * @returns {Promise}
 */
export function listTrainStation(params = {}) {
  const { page = 1, size = 10, keyword } = params
  return http.get(MODULE_PREFIX + '/admin/train-station/list', {
    params: {
      page,
      size,
      keyword
    }
  })
}

export function saveTrainStation(data) {
  return http.post(MODULE_PREFIX + '/admin/train-station/save', data)
}

export function deleteTrainStation(id) {
  return http.delete(MODULE_PREFIX + '/admin/train-station/' + id)
}
