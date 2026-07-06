<template>
  <div class="page">
    <div>
      <h1 class="page-title">统计报表</h1>
      <div class="page-description">汇总学生、教师、课程、选课和成绩数据。</div>
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
      <template #header><span class="section-title">教师工作量分布</span></template>
      <div ref="teachingLoadChartRef" class="chart chart--wide"></div>
    </el-card>

    <el-card>
      <template #header><span class="section-title">教师任课工作量</span></template>
      <div class="toolbar">
        <div class="toolbar-left">
          <el-input v-model="keyword" placeholder="搜索教师姓名" clearable style="width: 220px" />
          <div class="filter-inline">
            <span class="muted">最少任课</span>
            <el-input-number v-model="minTaskCount" :min="0" controls-position="right" style="width: 160px" />
          </div>
        </div>
      </div>

      <el-table v-loading="loading" :data="filteredTeachingLoad" border empty-text="暂无任课统计数据">
        <el-table-column prop="teacherName" label="教师" />
        <el-table-column prop="taskCount" label="任课数量" width="120" />
        <el-table-column prop="selectedCount" label="选课学生数" width="140" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import * as echarts from 'echarts'
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { getCollegeStudentsReport, getGradeDistributionReport, getOverviewReport, getTeachingLoadReport } from '../../api/report'
import { BUCKET_COLORS, LEVEL_COLORS } from '../../utils/academicMetrics'

const loading = ref(false)
const overview = ref({})
const collegeRows = ref([])
const gradeRows = ref([])
const teachingLoad = ref([])
const keyword = ref('')
const minTaskCount = ref(0)
const collegeChartRef = ref()
const gradeChartRef = ref()
const teachingLoadChartRef = ref()
let collegeChart = null
let gradeChart = null
let teachingLoadChart = null

const reportCards = computed(() => [
  { title: '学生总数', value: overview.value.studentCount ?? 0, extra: '正常学籍学生' },
  { title: '教师总数', value: overview.value.teacherCount ?? 0, extra: '正常在职教师' },
  { title: '课程总数', value: overview.value.courseCount ?? 0, extra: '启用课程' },
  { title: '选课记录', value: overview.value.selectionCount ?? 0, extra: '当前有效选课' }
])
const filteredTeachingLoad = computed(() =>
  teachingLoad.value.filter((item) => {
    const matchKeyword = !keyword.value || String(item.teacherName || '').includes(keyword.value)
    const matchTaskCount = Number(item.taskCount || 0) >= Number(minTaskCount.value || 0)
    return matchKeyword && matchTaskCount
  })
)

watch(filteredTeachingLoad, () => nextTick(renderCharts), { deep: true })
onMounted(refresh)
onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  disposeCharts()
})

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
    window.addEventListener('resize', handleResize)
  } finally {
    loading.value = false
  }
}

function renderCharts() {
  if (!collegeChartRef.value || !gradeChartRef.value || !teachingLoadChartRef.value) return
  collegeChart = echarts.getInstanceByDom(collegeChartRef.value) || echarts.init(collegeChartRef.value)
  gradeChart = echarts.getInstanceByDom(gradeChartRef.value) || echarts.init(gradeChartRef.value)
  teachingLoadChart = echarts.getInstanceByDom(teachingLoadChartRef.value) || echarts.init(teachingLoadChartRef.value)

  collegeChart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: 52, right: 24, top: 40, bottom: 48, containLabel: true },
    xAxis: { type: 'category', axisLabel: { interval: 0, rotate: 15 }, data: collegeRows.value.map((item) => item.name) },
    yAxis: { type: 'value', name: '学生人数' },
    series: [{ type: 'bar', data: collegeRows.value.map((item) => item.value), itemStyle: { color: '#2563eb' } }]
  })

  gradeChart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: 0, left: 'center' },
    color: Object.values(LEVEL_COLORS),
    series: [{ type: 'pie', radius: ['42%', '70%'], data: normalizeGradeRows(gradeRows.value) }]
  })

  teachingLoadChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { top: 0 },
    grid: { left: 48, right: 24, top: 42, bottom: 56, containLabel: true },
    xAxis: { type: 'category', axisLabel: { rotate: 20 }, data: filteredTeachingLoad.value.map((item) => item.teacherName) },
    yAxis: { type: 'value' },
    series: [
      { name: '任课数量', type: 'bar', data: filteredTeachingLoad.value.map((item) => item.taskCount), itemStyle: { color: BUCKET_COLORS[1] } },
      { name: '选课人数', type: 'bar', data: filteredTeachingLoad.value.map((item) => item.selectedCount), itemStyle: { color: BUCKET_COLORS[3] } }
    ]
  })
}

function normalizeGradeRows(rows) {
  const labelMap = { excellent: '优秀', good: '良好', medium: '及格', pass: '及格', fail: '不及格' }
  return rows.map((item) => {
    const name = labelMap[item.name] || item.name
    return { name, value: item.value, itemStyle: { color: LEVEL_COLORS[name] || '#64748b' } }
  })
}

function handleResize() {
  collegeChart?.resize()
  gradeChart?.resize()
  teachingLoadChart?.resize()
}

function disposeCharts() {
  collegeChart?.dispose()
  gradeChart?.dispose()
  teachingLoadChart?.dispose()
  collegeChart = null
  gradeChart = null
  teachingLoadChart = null
}
</script>
