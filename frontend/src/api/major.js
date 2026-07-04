import request from '../utils/request'

export function listMajors() {
  return request.get('/majors')
}

export function getMajor(id) {
  return request.get(`/majors/${id}`)
}

export function createMajor(data) {
  return request.post('/majors', data)
}

export function updateMajor(id, data) {
  return request.put(`/majors/${id}`, data)
}

export function removeMajor(id) {
  return request.delete(`/majors/${id}`)
}
