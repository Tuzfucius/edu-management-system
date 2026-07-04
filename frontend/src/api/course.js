import request from '../utils/request'

export function listCourses() {
  return request.get('/courses')
}

export function getCourse(id) {
  return request.get(`/courses/${id}`)
}

export function createCourse(data) {
  return request.post('/courses', data)
}

export function updateCourse(id, data) {
  return request.put(`/courses/${id}`, data)
}

export function removeCourse(id) {
  return request.delete(`/courses/${id}`)
}
