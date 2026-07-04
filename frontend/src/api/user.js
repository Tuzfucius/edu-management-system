import request from '../utils/request'

export function listUsers() {
  return request.get('/users')
}

export function createUser(data) {
  return request.post('/users', data)
}

export function updateUser(id, data) {
  return request.put(`/users/${id}`, data)
}

export function removeUser(id) {
  return request.delete(`/users/${id}`)
}
