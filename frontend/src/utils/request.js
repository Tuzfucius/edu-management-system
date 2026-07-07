import axios from 'axios'
import { ElMessage } from 'element-plus'

const appBase = import.meta.env.BASE_URL || '/'
const normalizedBase = appBase.endsWith('/') ? appBase : `${appBase}/`

function withBase(path) {
  return `${normalizedBase}${path.replace(/^\/+/, '')}`
}

function currentAppPath() {
  const pathname = window.location.pathname
  if (normalizedBase !== '/' && pathname.startsWith(normalizedBase.slice(0, -1))) {
    const stripped = pathname.slice(normalizedBase.slice(0, -1).length)
    return stripped || '/'
  }
  return pathname
}

function goAuthPage(path) {
  const target = withBase(path)
  if (window.location.pathname !== target) {
    window.location.assign(target)
  }
}

const request = axios.create({
  baseURL: withBase('api'),
  timeout: 10000,
  withCredentials: true
})

request.interceptors.response.use(
  (response) => {
    const res = response.data

    if (res.code !== 200) {
      if (res.code === 401) {
        localStorage.removeItem('currentUser')
        const path = currentAppPath()
        if (path !== '/login' && path !== '/401') {
          goAuthPage('login')
        }
      } else if (res.code === 403) {
        const path = currentAppPath()
        if (path !== '/401') {
          goAuthPage('401')
        }
      }
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(res)
    }

    return res.data
  },
  (error) => {
    const status = error.response?.status
    if (status === 401) {
      localStorage.removeItem('currentUser')
      const path = currentAppPath()
      if (path !== '/login' && path !== '/401') {
        goAuthPage('login')
      }
    } else if (status === 403) {
      const path = currentAppPath()
      if (path !== '/401') {
        goAuthPage('401')
      }
    }
    ElMessage.error(error.response?.data?.message || error.message || '网络错误')
    return Promise.reject(error)
  }
)

export default request
