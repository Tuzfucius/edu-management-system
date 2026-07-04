import request from '../utils/request'

export function listTeachingTasks(params = {}) {
  return request.get('/teaching-tasks', { params })
}

export function createTeachingTask(data) {
  return request.post('/teaching-tasks', data)
}

export function updateTeachingTask(id, data) {
  return request.put(`/teaching-tasks/${id}`, data)
}

export function removeTeachingTask(id) {
  return request.delete(`/teaching-tasks/${id}`)
}
