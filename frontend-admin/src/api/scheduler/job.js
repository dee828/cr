import http from '../http.js'

const MODULE_PREFIX = '/scheduler'

export function saveJob(data) {
  return http.post(MODULE_PREFIX + '/admin/job/save', data)
}

export function listJob() {
  return http.get(MODULE_PREFIX + '/admin/job/list')
}

export function deleteJob(data) {
  return http.post(MODULE_PREFIX + '/admin/job/delete', data)
}

export function pauseJob(data) {
  return http.post(MODULE_PREFIX + '/admin/job/pause', data)
}

export function resumeJob(data) {
  return http.post(MODULE_PREFIX + '/admin/job/resume', data)
}

export function rescheduleJob(data) {
  return http.post(MODULE_PREFIX + '/admin/job/reschedule', data)
}

export function runJob(data) {
  return http.post(MODULE_PREFIX + '/admin/job/run', data)
}
