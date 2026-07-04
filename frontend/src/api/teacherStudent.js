import request from '../utils/request'

export function listTeacherStudents() {
  return request.get('/teacher-students')
}

export function createTeacherStudent(data) {
  return request.post('/teacher-students', data)
}

export function updateTeacherStudent(id, data) {
  return request.put(`/teacher-students/${id}`, data)
}

export function removeTeacherStudent(id) {
  return request.delete(`/teacher-students/${id}`)
}
