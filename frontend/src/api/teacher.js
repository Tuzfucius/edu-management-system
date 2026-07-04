import request from '../utils/request'

export function listTeachers() {
  return request.get('/teachers')
}

export function getTeacherByUser(userId) {
  return request.get(`/teachers/by-user/${userId}`)
}

export function createTeacher(data) {
  return request.post('/teachers', data)
}

export function updateTeacher(id, data) {
  return request.put(`/teachers/${id}`, data)
}

export function removeTeacher(id) {
  return request.delete(`/teachers/${id}`)
}
