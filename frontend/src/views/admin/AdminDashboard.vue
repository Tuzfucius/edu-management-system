<template>
  <div class="page">
    <div>
      <h1 class="page-title">数据看板</h1>
      <div class="page-description">快速查看教务系统核心数据和近期建设状态。</div>
    </div>

    <div class="stat-grid">
      <el-card v-for="item in overviewStats.admin" :key="item.title" class="stat-card">
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
          <div v-for="bar in chartBars" :key="bar.name" class="chart-bar" :style="{ height: bar.height }" />
        </div>
      </el-card>

      <el-card>
        <template #header>
          <span class="section-title">近期任务</span>
        </template>
        <el-timeline>
          <el-timeline-item timestamp="当前阶段" type="success">三类角色前端界面搭建</el-timeline-item>
          <el-timeline-item timestamp="下一阶段">基础 CRUD 页面接入静态表单</el-timeline-item>
          <el-timeline-item timestamp="后续阶段">后端 Controller 完成后联调接口</el-timeline-item>
        </el-timeline>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { overviewStats } from '../../data/mockData'

const chartBars = [
  { name: '计算机', height: '84%' },
  { name: '人工智能', height: '62%' },
  { name: '软件工程', height: '48%' },
  { name: '数据科学', height: '56%' },
  { name: '网络空间', height: '38%' }
]
</script>
