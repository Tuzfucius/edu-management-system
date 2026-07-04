<template>
  <div class="page">
    <div>
      <h1 class="page-title">统计报表</h1>
      <div class="page-description">基于真实数据库汇总学生、教师、课程、选课和成绩数据。</div>
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
        <template #header><span class="section-title">学院学生人数</span></template>
        <div ref="collegeChartRef" class="chart"></div>
      </el-card>
      <el-card>
        <template #header><span class="section-title">成绩分布</span></template>
        <div ref="gradeChartRef" class="chart"></div>
      </el-card>
    </div>

    <el-card>
      <template #header><span class="section-title">教师任课工作量</span></template>
      <el-table v-loading="loading" :data="teachingLoad" border empty-text="暂无任课统计数据">
        <el-table-column prop="teacherName" label="教师" />
        <el-table-column prop="taskCount" label="任课数量" width="120" />
        <el-table-column prop="selectedCount" label="选课学生数" width="140" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import * as echarts from 'echarts'
import { computed, nextTick, onMounted, ref } from 'vue'
import { getCollegeStudentsReport, getGradeDistributionReport, getOverviewReport, getTeachingLoadReport } from '../../api/report'

const loading = ref(false)
const overview = ref({})
const collegeRows = ref([])
const gradeRows = ref([])
const teachingLoad = ref([])
const collegeChartRef = ref()
const gradeChartRef = ref()

const reportCards = computed(() => [
  { title: '学生总数', value: overview.value.studentCount ?? 0, extra: '正常学籍学生' },
  { title: '教师总数', value: overview.value.teacherCount ?? 0, extra: '正常在职教师' },
  { title: '课程总数', value: overview.value.courseCount ?? 0, extra: '启用课程' },
  { title: '选课记录', value: overview.value.selectionCount ?? 0, extra: '当前有效选课' }
])

onMounted(refresh)

async function refresh() {
  loading.value = true
  try {
    ;[overview.value, collegeRows.value, gradeRows.value, teachingLoad.value] = await Promise.all([
      getOverviewReport(),
      getCollegeStudentsReport(),
      getGradeDistributionReport(),
      getTeachingLoadReport()
    ])
    await nextTick()
    renderCharts()
  } finally {
    loading.value = false
  }
}

function renderCharts() {
  const collegeChart = echarts.init(collegeChartRef.value)
  collegeChart.setOption({
    tooltip: {},
    xAxis: { type: 'category', data: collegeRows.value.map((item) => item.name) },
    yAxis: { type: 'value' },
    series: [{ type: 'bar', data: collegeRows.value.map((item) => item.value), itemStyle: { color: '#2563eb' } }]
  })

  const gradeChart = echarts.init(gradeChartRef.value)
  gradeChart.setOption({
    tooltip: { trigger: 'item' },
    series: [{ type: 'pie', radius: ['42%', '70%'], data: gradeRows.value }]
  })
}
</script>

<style scoped>
.chart {
  width: 100%;
  height: 280px;
}
</style>
