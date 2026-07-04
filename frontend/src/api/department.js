import request from '../utils/request'

export function listDepartments() {
  return request.get('/departments')
}

export function getDepartment(id) {
  return request.get(`/departments/${id}`)
}

export function createDepartment(data) {
  return request.post('/departments', data)
}

export function updateDepartment(id, data) {
  return request.put(`/departments/${id}`, data)
}

export function removeDepartment(id) {
  return request.delete(`/departments/${id}`)
}
