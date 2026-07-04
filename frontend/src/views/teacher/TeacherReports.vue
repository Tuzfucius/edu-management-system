<template>
  <div class="page">
    <div>
      <h1 class="page-title">成绩分析</h1>
      <div class="page-description">查看本人任课课程的成绩分布和完成情况。</div>
    </div>

    <div class="stat-grid">
      <el-card v-for="item in cards" :key="item.title" class="stat-card">
        <div class="stat-title">{{ item.title }}</div>
        <div class="stat-number">{{ item.value }}</div>
        <div class="stat-extra">{{ item.extra }}</div>
      </el-card>
    </div>

    <div class="content-grid">
      <el-card>
        <template #header><span class="section-title">数据库原理成绩分布</span></template>
        <div class="chart-placeholder">
          <div v-for="bar in ['82%', '68%', '44%', '24%']" :key="bar" class="chart-bar" :style="{ height: bar }" />
        </div>
      </el-card>

      <el-card>
        <template #header><span class="section-title">课程完成率</span></template>
        <el-space direction="vertical" fill style="width: 100%">
          <div>
            <div class="toolbar"><span>数据库原理</span><span class="muted">76%</span></div>
            <el-progress :percentage="76" />
          </div>
          <div>
            <div class="toolbar"><span>Java Web 开发</span><span class="muted">31%</span></div>
            <el-progress :percentage="31" status="warning" />
          </div>
          <div>
            <div class="toolbar"><span>数据库课程设计</span><span class="muted">100%</span></div>
            <el-progress :percentage="100" status="success" />
          </div>
        </el-space>
      </el-card>
    </div>
  </div>
</template>

<script setup>
const cards = [
  { title: '平均分', value: '84.6', extra: '已录入课程统计' },
  { title: '最高分', value: '96', extra: '数据库原理' },
  { title: '及格率', value: '94%', extra: '低于 60 分视为未通过' },
  { title: '待录入', value: '48', extra: '跨 2 门课程' }
]
</script>
