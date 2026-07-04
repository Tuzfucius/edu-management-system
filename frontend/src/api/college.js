import request from '../utils/request'

export function listColleges() {
  return request.get('/colleges')
}

export function getCollege(id) {
  return request.get(`/colleges/${id}`)
}

export function createCollege(data) {
  return request.post('/colleges', data)
}

export function updateCollege(id, data) {
  return request.put(`/colleges/${id}`, data)
}

export function removeCollege(id) {
  return request.delete(`/colleges/${id}`)
}
