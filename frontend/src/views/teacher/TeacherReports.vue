<template>
  <div class="page" v-loading="loading">
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

    <el-card>
      <div class="toolbar">
        <div class="toolbar-left">
          <el-select v-model="taskId" clearable placeholder="选择课程" style="min-width: 280px">
            <el-option label="全部课程" :value="''" />
            <el-option v-for="item in taskOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
          <el-input v-model="keyword" placeholder="搜索学号或姓名" clearable style="width: 220px" />
        </div>
      </div>

      <div class="content-grid">
        <el-card>
          <template #header><span class="section-title">{{ selectedTaskLabel }}成绩分布</span></template>
          <div ref="barRef" class="chart chart--wide"></div>
        </el-card>

        <el-card>
          <template #header><span class="section-title">成绩录入占比</span></template>
          <div ref="pieRef" class="chart chart--small"></div>
        </el-card>
      </div>

      <el-table :data="displayRows" border empty-text="暂无成绩记录">
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="studentName" label="姓名" width="100" />
        <el-table-column prop="courseName" label="课程" />
        <el-table-column prop="semester" label="学期" width="130" />
        <el-table-column label="成绩" width="100">
          <template #default="{ row }">{{ row.score ?? '-' }}</template>
        </el-table-column>
        <el-table-column label="状态" width="110">
          <template #default="{ row }">
            <el-tag :type="Number(row.gradeStatus) === 1 ? 'success' : 'warning'" effect="plain">
              {{ Number(row.gradeStatus) === 1 ? '已录入' : '未录入' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import * as echarts from 'echarts'
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { getTeacherByUser } from '../../api/teacher'
import { listStudentCourses } from '../../api/studentCourse'
import { averageScore, buildScoreBuckets, BUCKET_COLORS } from '../../utils/academicMetrics'
import { filterRows } from '../../utils/filter'

const loading = ref(false)
const rows = ref([])
const currentUser = JSON.parse(localStorage.getItem('currentUser') || 'null')
const taskId = ref('')
const keyword = ref('')
const barRef = ref(null)
const pieRef = ref(null)
let barChart = null
let pieChart = null

const taskOptions = computed(() => {
  const seen = new Map()
  rows.value.forEach((row) => {
    if (!seen.has(row.teachingTaskId)) {
      seen.set(row.teachingTaskId, { value: row.teachingTaskId, label: `${row.courseName} · ${row.semester}` })
    }
  })
  return Array.from(seen.values())
})
const selectedTaskLabel = computed(() => taskOptions.value.find((item) => String(item.value) === String(taskId.value))?.label || '全部课程')
const displayRows = computed(() =>
  filterRows(rows.value, {
    keyword: keyword.value,
    fields: ['studentNo', 'studentName', 'courseCode', 'courseName', 'semester'],
    predicates: [(row) => !taskId.value || String(row.teachingTaskId) === String(taskId.value)]
  })
)
const gradedRows = computed(() => displayRows.value.filter((row) => Number(row.gradeStatus) === 1 && row.score !== null && row.score !== undefined))
const pendingRows = computed(() => displayRows.value.filter((row) => Number(row.gradeStatus) !== 1))
const cards = computed(() => {
  const passRate = gradedRows.value.length
    ? `${Math.round((gradedRows.value.filter((row) => Number(row.score) >= 60).length / gradedRows.value.length) * 100)}%`
    : '-'
  return [
    { title: '选课人数', value: displayRows.value.length, extra: '当前筛选课程' },
    { title: '已录成绩', value: gradedRows.value.length, extra: `未录 ${pendingRows.value.length} 人` },
    { title: '平均分', value: averageScore(gradedRows.value), extra: '仅统计已录入成绩' },
    { title: '及格率', value: passRate, extra: '60 分及以上' }
  ]
})

onMounted(refresh)
watch(displayRows, () => nextTick(renderCharts), { deep: true })
onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  disposeCharts()
})

async function refresh() {
  loading.value = true
  try {
    const teacher = await getTeacherByUser(currentUser.id)
    rows.value = await listStudentCourses({ teacherId: teacher.id })
    taskId.value = taskOptions.value[0]?.value ?? ''
    await nextTick()
    renderCharts()
    window.addEventListener('resize', handleResize)
  } finally {
    loading.value = false
  }
}

function renderCharts() {
  if (!barRef.value || !pieRef.value) return
  barChart = echarts.getInstanceByDom(barRef.value) || echarts.init(barRef.value)
  pieChart = echarts.getInstanceByDom(pieRef.value) || echarts.init(pieRef.value)
  const buckets = buildScoreBuckets(gradedRows.value)
  barChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 44, right: 20, top: 32, bottom: 42, containLabel: true },
    xAxis: { type: 'category', data: buckets.map((item) => item.name) },
    yAxis: { type: 'value', name: '人数' },
    series: [{ type: 'bar', data: buckets.map((item, index) => ({ value: item.value, itemStyle: { color: BUCKET_COLORS[index] } })) }]
  })
  pieChart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: 0, left: 'center' },
    series: [
      {
        type: 'pie',
        radius: ['42%', '70%'],
        center: ['50%', '42%'],
        label: { formatter: '{b}\n{d}%' },
        data: [
          { name: '已录入', value: gradedRows.value.length, itemStyle: { color: '#16a34a' } },
          { name: '未录入', value: pendingRows.value.length, itemStyle: { color: '#f59e0b' } }
        ]
      }
    ]
  })
}

function handleResize() {
  barChart?.resize()
  pieChart?.resize()
}

function disposeCharts() {
  barChart?.dispose()
  pieChart?.dispose()
  barChart = null
  pieChart = null
}
</script>
