import request from '../utils/request'

export function listClassInfos() {
  return request.get('/classes')
}

export function getClassInfo(id) {
  return request.get(`/classes/${id}`)
}

export function createClassInfo(data) {
  return request.post('/classes', data)
}

export function updateClassInfo(id, data) {
  return request.put(`/classes/${id}`, data)
}

export function removeClassInfo(id) {
  return request.delete(`/classes/${id}`)
}
