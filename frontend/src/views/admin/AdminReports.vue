<template>
  <div class="page">
    <div>
      <h1 class="page-title">统计报表</h1>
      <div class="page-description">面向答辩展示的教务数据统计与图形分析入口。</div>
    </div>

    <div class="stat-grid">
      <el-card v-for="item in reportCards" :key="item.title" class="stat-card">
        <div class="stat-title">{{ item.title }}</div>
        <div class="stat-number">{{ item.value }}</div>
        <div class="stat-extra">{{ item.extra }}</div>
      </el-card>
    </div>

    <div class="content-grid">
      <el-card>
        <template #header><span class="section-title">课程选课人数 Top 5</span></template>
        <div class="chart-placeholder">
          <div v-for="bar in ['70%', '62%', '58%', '52%', '40%']" :key="bar" class="chart-bar" :style="{ height: bar }" />
        </div>
      </el-card>
      <el-card>
        <template #header><span class="section-title">成绩分布</span></template>
        <el-progress :percentage="28" status="success" />
        <el-progress :percentage="36" />
        <el-progress :percentage="22" status="warning" />
        <el-progress :percentage="14" status="exception" />
        <div class="stat-extra">优秀、良好、中等、待提升四类成绩占比。</div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
const reportCards = [
  { title: '学院学生统计', value: '5 项', extra: '按学院与专业汇总' },
  { title: '课程选课统计', value: '58 门', extra: '本学期开课课程' },
  { title: '成绩分布统计', value: '4 档', extra: '优秀到不及格' },
  { title: '教师任课统计', value: '86 人', extra: '支持按教研室筛选' }
]
</script>
