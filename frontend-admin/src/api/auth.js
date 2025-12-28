import http from './http.js'

const MODULE_PREFIX = '/user'

export function login(email, password) {
  return http.post(MODULE_PREFIX + '/auth/login', {email: email, password: password})
}
