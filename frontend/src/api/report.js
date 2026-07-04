import request from '../utils/request'

export function getOverviewReport() {
  return request.get('/reports/overview')
}

export function getCollegeStudentsReport() {
  return request.get('/reports/college-students')
}

export function getGradeDistributionReport() {
  return request.get('/reports/grade-distribution')
}

export function getTeachingLoadReport() {
  return request.get('/reports/teaching-load')
}
