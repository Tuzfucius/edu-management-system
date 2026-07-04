<template>
  <main class="error-page">
    <section class="error-panel">
      <div class="error-code">404</div>
      <h1>页面不存在</h1>
      <p>当前地址没有对应页面，请返回系统首页继续操作。</p>
      <el-button type="primary" @click="goHome">返回首页</el-button>
    </section>
  </main>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

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
  color: #2563eb;
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
</style>

