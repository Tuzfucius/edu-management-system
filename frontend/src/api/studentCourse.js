import request from '../utils/request'

export function listStudentCourses(params = {}) {
  return request.get('/student-courses', { params })
}

export function listSelectableCourses(params) {
  return request.get('/student-courses/selectable', { params })
}

export function selectCourse(data) {
  return request.post('/student-courses', data)
}

export function dropCourse(id) {
  return request.delete(`/student-courses/${id}`)
}

export function updateScore(id, data) {
  return request.put(`/student-courses/${id}/score`, data)
}
