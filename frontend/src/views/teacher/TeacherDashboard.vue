<template>
  <div class="page" v-loading="loading">
    <div>
      <h1 class="page-title">教师工作台</h1>
      <div class="page-description">查看本学期任课、学生规模和成绩录入进度。</div>
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
        <template #header><span class="section-title">本周课程</span></template>
        <el-table :data="tasks" border empty-text="暂无任课安排">
          <el-table-column prop="course" label="课程" />
          <el-table-column prop="time" label="时间" width="130" />
          <el-table-column prop="room" label="教室" width="120" />
          <el-table-column prop="selected" label="学生数" width="90" />
        </el-table>
      </el-card>

      <el-card>
        <template #header><span class="section-title">成绩录入进度</span></template>
        <el-space direction="vertical" fill style="width: 100%">
          <div v-for="task in tasks" :key="task.id">
            <div class="toolbar">
              <span>{{ task.course }}</span>
              <span class="muted">待录 {{ task.pending }} 人</span>
            </div>
            <el-progress :percentage="progress(task)" />
          </div>
        </el-space>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { getTeacherDashboardData } from '../../data/mockData'

const loading = ref(false)
const stats = ref([])
const tasks = ref([])

onMounted(refresh)

async function refresh() {
  loading.value = true
  try {
    const data = await getTeacherDashboardData()
    stats.value = data.stats
    tasks.value = data.tasks
  } finally {
    loading.value = false
  }
}

function progress(task) {
  if (!task.selected) return 0
  return Math.round(((task.selected - task.pending) / task.selected) * 100)
}
</script>
