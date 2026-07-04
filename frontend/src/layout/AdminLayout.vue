<template>
  <el-container class="layout">
    <el-aside width="220px" class="aside">
      <div class="logo">教务管理系统</div>

      <el-menu
          router
          :default-active="route.path"
          background-color="#1f2937"
          text-color="#d1d5db"
          active-text-color="#ffffff"
      >
        <el-menu-item
            v-for="item in currentMenu"
            :key="item.path"
            :index="item.path"
        >
          <el-icon>
            <component :is="item.icon" />
          </el-icon>
          <span>{{ item.label }}</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div>
          <div class="header-title">{{ route.meta.title || roleConfig.title }}</div>
          <div class="header-subtitle">{{ roleConfig.subtitle }}</div>
        </div>

        <div class="header-right">
          <el-tag :type="roleConfig.tagType" effect="plain">{{ roleConfig.roleName }}</el-tag>
          <span class="user-info">当前用户：{{ currentUser.displayName }}</span>
          <el-button type="danger" size="small" @click="logout">退出登录</el-button>
        </div>
      </el-header>

      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  Collection,
  DataBoard,
  User,
  Reading,
  PieChart,
  Tickets,
  Notebook,
  EditPen,
  School,
  TrendCharts
} from '@element-plus/icons-vue'
import { logout as logoutApi } from '../api/auth'

const router = useRouter()
const route = useRoute()

const currentUser = computed(() => {
  return JSON.parse(localStorage.getItem('currentUser') || 'null') || {
    role: 'ADMIN',
    displayName: '管理员'
  }
})

const menus = {
  ADMIN: [
    { label: '数据看板', path: '/admin/dashboard', icon: DataBoard },
    { label: '组织管理', path: '/admin/academic', icon: Collection },
    { label: '人员管理', path: '/admin/people', icon: User },
    { label: '课程管理', path: '/admin/courses', icon: Reading },
    { label: '统计报表', path: '/admin/reports', icon: PieChart }
  ],
  TEACHER: [
    { label: '教师工作台', path: '/teacher/dashboard', icon: DataBoard },
    { label: '我的任课', path: '/teacher/courses', icon: Notebook },
    { label: '成绩录入', path: '/teacher/grades', icon: EditPen },
    { label: '成绩分析', path: '/teacher/reports', icon: TrendCharts }
  ],
  STUDENT: [
    { label: '学生工作台', path: '/student/dashboard', icon: School },
    { label: '可选课程', path: '/student/course-select', icon: Reading },
    { label: '我的成绩', path: '/student/my-grades', icon: Tickets }
  ]
}

const roleConfigs = {
  ADMIN: {
    title: '后台管理',
    subtitle: '基础数据、人员、课程与报表维护',
    roleName: '管理员',
    tagType: 'danger'
  },
  TEACHER: {
    title: '教师端',
    subtitle: '任课查看、学生名单与成绩录入',
    roleName: '教师',
    tagType: 'warning'
  },
  STUDENT: {
    title: '学生端',
    subtitle: '课程选择、个人课程与成绩查询',
    roleName: '学生',
    tagType: 'success'
  }
}

const currentMenu = computed(() => menus[currentUser.value.role] || menus.ADMIN)
const roleConfig = computed(() => roleConfigs[currentUser.value.role] || roleConfigs.ADMIN)

async function logout() {
  try {
    await logoutApi()
  } catch (error) {
    // 本地状态仍需清理，避免会话接口异常时卡在当前页面。
  }
  localStorage.removeItem('currentUser')
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

.header-title {
  color: #111827;
  font-size: 16px;
  font-weight: 600;
}

.header-subtitle {
  margin-top: 4px;
  color: #9ca3af;
  font-size: 12px;
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

@media (max-width: 900px) {
  .aside {
    width: 188px !important;
  }

  .header {
    height: auto;
    min-height: 56px;
    align-items: flex-start;
    flex-direction: column;
    gap: 8px;
    padding: 12px 16px;
  }

  .main {
    padding: 16px;
  }
}
</style>
