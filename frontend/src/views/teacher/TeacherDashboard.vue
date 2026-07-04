<template>
  <div class="page">
    <div>
      <h1 class="page-title">教师工作台</h1>
      <div class="page-description">查看本学期任课、学生规模和成绩录入进度。</div>
    </div>

    <div class="stat-grid">
      <el-card v-for="item in overviewStats.teacher" :key="item.title" class="stat-card">
        <div class="stat-title">{{ item.title }}</div>
        <div class="stat-number">{{ item.value }}</div>
        <div class="stat-extra">{{ item.extra }}</div>
      </el-card>
    </div>

    <div class="content-grid">
      <el-card>
        <template #header><span class="section-title">本周课程</span></template>
        <el-table :data="teacherTasks" border>
          <el-table-column prop="course" label="课程" />
          <el-table-column prop="time" label="时间" width="130" />
          <el-table-column prop="room" label="教室" width="120" />
          <el-table-column prop="selected" label="学生数" width="90" />
        </el-table>
      </el-card>

      <el-card>
        <template #header><span class="section-title">成绩录入进度</span></template>
        <el-space direction="vertical" fill style="width: 100%">
          <div v-for="task in teacherTasks" :key="task.course">
            <div class="toolbar">
              <span>{{ task.course }}</span>
              <span class="muted">待录 {{ task.pending }} 人</span>
            </div>
            <el-progress :percentage="task.pending === 0 ? 100 : 70" />
          </div>
        </el-space>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { overviewStats, teacherTasks } from '../../data/mockData'
</script>
