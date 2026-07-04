<template>
  <main class="error-page">
    <section class="error-panel">
      <div class="error-code">401</div>
      <h1>无权访问</h1>
      <p>当前登录状态已失效，或当前角色没有访问该页面的权限。</p>
      <div class="actions">
        <el-button type="primary" @click="goLogin">重新登录</el-button>
        <el-button @click="goHome">返回首页</el-button>
      </div>
    </section>
  </main>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

function goLogin() {
  localStorage.removeItem('currentUser')
  router.push('/login')
}

function goHome() {
  const user = JSON.parse(localStorage.getItem('currentUser') || 'null')
  router.push(user ? `/${user.role.toLowerCase()}/dashboard` : '/login')
}
</script>

<style scoped>
.error-page {
  display: flex;
  min-height: 100vh;
  align-items: center;
  justify-content: center;
  padding: 32px;
  background: #f3f4f6;
}

.error-panel {
  width: min(480px, 100%);
  text-align: center;
}

.error-code {
  color: #d97706;
  font-size: 64px;
  font-weight: 700;
  line-height: 1;
}

h1 {
  margin: 16px 0 8px;
  color: #111827;
  font-size: 24px;
}

p {
  margin: 0 0 24px;
  color: #6b7280;
  font-size: 14px;
}

.actions {
  display: flex;
  justify-content: center;
  gap: 12px;
}
</style>

