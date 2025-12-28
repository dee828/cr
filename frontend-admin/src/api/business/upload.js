import http from '../http.js'

export const MODULE_PREFIX = '/business'

export function upload(data, config = {}) {
  return http({
    url: MODULE_PREFIX + '/admin/file/upload',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    onUploadProgress: config.onUploadProgress
  })
}
