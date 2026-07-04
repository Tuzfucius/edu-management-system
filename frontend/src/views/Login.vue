<template>
  <div class="login-page">
    <el-card class="login-card">
      <div class="title-area">
        <h2>教务管理系统</h2>
        <p>Education Management System</p>
      </div>

      <el-form label-position="top">
        <el-form-item label="用户名">
          <el-input
              v-model="loginForm.username"
              placeholder="请输入用户名"
              clearable
          />
        </el-form-item>

        <el-form-item label="密码">
          <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              show-password
          />
        </el-form-item>

        <el-form-item label="角色">
          <el-select
              v-model="loginForm.role"
              placeholder="请选择登录角色"
              style="width: 100%"
          >
            <el-option label="管理员" value="ADMIN" />
            <el-option label="教师" value="TEACHER" />
            <el-option label="学生" value="STUDENT" />
          </el-select>
        </el-form-item>

        <el-button type="primary" class="login-button" @click="handleLogin">
          登录
        </el-button>
      </el-form>

      <div class="quick-login">
        <span>演示账号</span>
        <el-button link type="primary" @click="fillDemo('ADMIN')">管理员</el-button>
        <el-button link type="primary" @click="fillDemo('TEACHER')">教师</el-button>
        <el-button link type="primary" @click="fillDemo('STUDENT')">学生</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()

const loginForm = reactive({
  username: '',
  password: '',
  role: ''
})

const demoUsers = {
  ADMIN: { username: 'admin', displayName: '系统管理员' },
  TEACHER: { username: 'teacher01', displayName: '张老师' },
  STUDENT: { username: 'student01', displayName: '李同学' }
}

function fillDemo(role) {
  loginForm.username = demoUsers[role].username
  loginForm.password = '123456'
  loginForm.role = role
}

function handleLogin() {
  if (!loginForm.username) {
    ElMessage.warning('请输入用户名')
    return
  }

  if (!loginForm.password) {
    ElMessage.warning('请输入密码')
    return
  }

  if (!loginForm.role) {
    ElMessage.warning('请选择角色')
    return
  }

  const demoUser = demoUsers[loginForm.role] || {
    username: loginForm.username,
    displayName: loginForm.username
  }

  localStorage.setItem('currentUser', JSON.stringify({
    username: loginForm.username,
    displayName: demoUser.displayName,
    role: loginForm.role
  }))

  ElMessage.success('登录成功')
  router.push(`/${loginForm.role.toLowerCase()}/dashboard`)
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: #f3f4f6;
  display: flex;
  justify-content: center;
  align-items: center;
}

.login-card {
  width: 380px;
  border-radius: 8px;
}

.title-area {
  text-align: center;
  margin-bottom: 24px;
}

.title-area h2 {
  margin: 0;
  font-size: 24px;
  color: #1f2937;
}

.title-area p {
  margin-top: 8px;
  color: #6b7280;
  font-size: 14px;
}

.login-button {
  width: 100%;
  height: 40px;
}

.quick-login {
  margin-top: 16px;
  padding-top: 12px;
  border-top: 1px solid #e5e7eb;
  color: #6b7280;
  font-size: 13px;
  text-align: center;
}
</style>
