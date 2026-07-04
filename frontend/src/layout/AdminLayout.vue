<template>
  <el-container class="layout">
    <el-aside width="220px" class="aside">
      <div class="logo">教务管理系统</div>

      <el-menu
          router
          default-active="/dashboard"
          background-color="#1f2937"
          text-color="#d1d5db"
          active-text-color="#ffffff"
      >
        <el-menu-item index="/dashboard">
          <el-icon><DataBoard /></el-icon>
          <span>数据看板</span>
        </el-menu-item>

        <el-menu-item index="/students">
          <el-icon><User /></el-icon>
          <span>学生管理</span>
        </el-menu-item>

        <el-menu-item index="/teachers">
          <el-icon><Avatar /></el-icon>
          <span>教师管理</span>
        </el-menu-item>

        <el-menu-item index="/courses">
          <el-icon><Reading /></el-icon>
          <span>课程管理</span>
        </el-menu-item>

        <el-menu-item index="/reports">
          <el-icon><PieChart /></el-icon>
          <span>统计报表</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div>后台管理</div>

        <div class="header-right">
          <span class="user-info">当前用户：admin</span>
          <el-button type="danger" size="small" @click="logout">
            退出登录
          </el-button>
        </div>
      </el-header>

      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRouter } from 'vue-router'
import {
  DataBoard,
  User,
  Avatar,
  Reading,
  PieChart
} from '@element-plus/icons-vue'

const router = useRouter()

function logout() {
  router.push('/login')
}
</script>

<style scoped>
.layout {
  min-height: 100vh;
}

.aside {
  background: #1f2937;
}

.logo {
  height: 56px;
  line-height: 56px;
  color: white;
  font-weight: bold;
  text-align: center;
  border-bottom: 1px solid #374151;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  border-bottom: 1px solid #e5e7eb;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-info {
  color: #6b7280;
  font-size: 14px;
}

.main {
  background: #f3f4f6;
  padding: 20px;
}
</style>