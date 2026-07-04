<template>
  <div class="page">
    <div>
      <h1 class="page-title">学生工作台</h1>
      <div class="page-description">查看课程、成绩和个人培养进度。</div>
    </div>

    <div class="stat-grid">
      <el-card v-for="item in overviewStats.student" :key="item.title" class="stat-card">
        <div class="stat-title">{{ item.title }}</div>
        <div class="stat-number">{{ item.value }}</div>
        <div class="stat-extra">{{ item.extra }}</div>
      </el-card>
    </div>

    <div class="content-grid">
      <el-card>
        <template #header><span class="section-title">今日课程</span></template>
        <el-table :data="todayCourses" border empty-text="今日暂无课程">
          <el-table-column prop="name" label="课程" />
          <el-table-column prop="time" label="时间" width="140" />
          <el-table-column prop="room" label="地点" width="130" />
        </el-table>
      </el-card>

      <el-card>
        <template #header><span class="section-title">个人信息</span></template>
        <el-descriptions :column="1" border>
          <el-descriptions-item label="学号">20240101</el-descriptions-item>
          <el-descriptions-item label="姓名">李同学</el-descriptions-item>
          <el-descriptions-item label="班级">软工 2401</el-descriptions-item>
          <el-descriptions-item label="专业">软件工程</el-descriptions-item>
        </el-descriptions>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { overviewStats } from '../../data/mockData'

const todayCourses = [
  { name: '数据库原理', time: '周二 3-4 节', room: 'A302' },
  { name: '大学英语', time: '周二 7-8 节', room: 'C105' }
]
</script>
