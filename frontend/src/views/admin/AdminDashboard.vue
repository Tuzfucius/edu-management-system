<template>
  <div class="page" v-loading="loading">
    <div>
      <h1 class="page-title">数据看板</h1>
      <div class="page-description">快速查看教务系统核心数据和近期建设状态。</div>
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
        <template #header>
          <span class="section-title">学院学生人数</span>
        </template>
        <div class="chart-placeholder">
          <div v-for="bar in chartBars" :key="bar.name" class="chart-bar" :style="{ height: bar.height }" :title="`${bar.name}: ${bar.value}`" />
        </div>
      </el-card>

      <el-card>
        <template #header>
          <span class="section-title">数据来源</span>
        </template>
        <el-timeline>
          <el-timeline-item timestamp="学生统计" type="success">读取 student 表启用记录</el-timeline-item>
          <el-timeline-item timestamp="教师与课程">读取 teacher、course、teaching_task 表</el-timeline-item>
          <el-timeline-item timestamp="学院分布">通过学院、专业、班级、学生关联统计</el-timeline-item>
        </el-timeline>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { getAdminDashboardData } from '../../data/mockData'

const loading = ref(false)
const stats = ref([])
const chartBars = ref([])

onMounted(refresh)

async function refresh() {
  loading.value = true
  try {
    const data = await getAdminDashboardData()
    stats.value = data.stats
    chartBars.value = data.chartBars
  } finally {
    loading.value = false
  }
}
</script>
