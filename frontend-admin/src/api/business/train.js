import http from '../http.js'

const MODULE_PREFIX = '/business'

/**
 * 获取车次列表
 * @param {Object} params 查询参数
 * @param {number} params.page 当前页码
 * @param {number} params.size 每页显示条数
 * @returns {Promise}
 */
export function listTrain(params = {}) {
  const { page = 1, size = 10, keyword } = params
  return http.get(MODULE_PREFIX + '/admin/train/list', {
    params: {
      page,
      size,
      keyword
    }
  })
}

export function saveTrain(data) {
  return http.post(MODULE_PREFIX + '/admin/train/save', data)
}

export function deleteTrain(id) {
  return http.delete(MODULE_PREFIX + '/admin/train/' + id)
}

export function genSeat(trainCode){
  return http.get(MODULE_PREFIX + '/admin/train/gen-seat/' + trainCode)
}

export function genDaily(date){
  return http.get(MODULE_PREFIX + '/admin/daily-train/gen-daily/' + date)
}
