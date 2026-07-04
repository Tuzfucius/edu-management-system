<template>
  <div class="page" v-loading="loading">
    <div>
      <h1 class="page-title">学生工作台</h1>
      <div class="page-description">查看课程、成绩和个人培养进度。</div>
    </div>

    <div class="stat-grid">
      <el-card v-for="item in stats" :key="item.title" class="stat-card">
        <div class="stat-title">{{ item.title }}</div>
        <div class="stat-number">{{ item.value }}</div>
        <div class="stat-extra">{{ item.extra }}</div>
      </el-card>
    </div>

    <div class="content-grid">
      <el-card>
        <template #header><span class="section-title">我的课程</span></template>
        <el-table :data="courses" border empty-text="暂无课程">
          <el-table-column prop="name" label="课程" />
          <el-table-column prop="time" label="时间" width="140" />
          <el-table-column prop="room" label="地点" width="130" />
        </el-table>
      </el-card>

      <el-card>
        <template #header><span class="section-title">个人信息</span></template>
        <el-descriptions :column="1" border>
          <el-descriptions-item label="学号">{{ student?.studentNo || '-' }}</el-descriptions-item>
          <el-descriptions-item label="姓名">{{ student?.studentName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="班级">{{ student?.className || '-' }}</el-descriptions-item>
          <el-descriptions-item label="专业">{{ student?.majorName || '-' }}</el-descriptions-item>
        </el-descriptions>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { getStudentDashboardData } from '../../data/mockData'

const loading = ref(false)
const stats = ref([])
const courses = ref([])
const student = ref(null)

onMounted(refresh)

async function refresh() {
  loading.value = true
  try {
    const data = await getStudentDashboardData()
    stats.value = data.stats
    courses.value = data.courses
    student.value = data.student
  } finally {
    loading.value = false
  }
}
</script>
