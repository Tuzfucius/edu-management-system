import request from '../utils/request'

export function listStudents() {
  return request.get('/students')
}

export function getStudentByUser(userId) {
  return request.get(`/students/by-user/${userId}`)
}

export function createStudent(data) {
  return request.post('/students', data)
}

export function updateStudent(id, data) {
  return request.put(`/students/${id}`, data)
}

export function removeStudent(id) {
  return request.delete(`/students/${id}`)
}
