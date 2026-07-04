import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000,
  withCredentials: true
})

request.interceptors.response.use(
  (response) => {
    const res = response.data

    if (res.code !== 200) {
      if (res.code === 401) {
        localStorage.removeItem('currentUser')
        if (window.location.pathname !== '/login' && window.location.pathname !== '/401') {
          window.location.assign('/401')
        }
      }
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(res)
    }

    return res.data
  },
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('currentUser')
      if (window.location.pathname !== '/login' && window.location.pathname !== '/401') {
        window.location.assign('/401')
      }
    }
    ElMessage.error(error.response?.data?.message || error.message || '网络错误')
    return Promise.reject(error)
  }
)

export default request
