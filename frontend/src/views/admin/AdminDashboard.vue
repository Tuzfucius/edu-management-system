<template>
  <div class="page" v-loading="loading">
    <div>
      <h1 class="page-title">数据看板</h1>
      <div class="page-description">快速查看教务系统核心数据和学院师生分布。</div>
    </div>

    <div class="stat-grid">
      <el-card v-for="item in stats" :key="item.title" class="stat-card">
        <div class="stat-title">{{ item.title }}</div>
        <div class="stat-number">{{ item.value }}</div>
        <div class="stat-extra">{{ item.extra }}</div>
      </el-card>
    </div>

    <div class="content-grid">
      <el-card class="chart-card">
        <template #header>
          <span class="section-title">学院师生人数对比</span>
        </template>
        <div ref="barRef" class="chart chart--wide"></div>
      </el-card>

      <el-card class="chart-card">
        <template #header>
          <span class="section-title">学院占比</span>
        </template>
        <div class="pie-grid">
          <div class="pie-panel">
            <div class="pie-panel__title">学生占比</div>
            <div ref="studentPieRef" class="chart chart--small"></div>
          </div>
          <div class="pie-panel">
            <div class="pie-panel__title">教师占比</div>
            <div ref="teacherPieRef" class="chart chart--small"></div>
          </div>
        </div>
      </el-card>
    </div>

    <el-card>
      <template #header>
        <span class="section-title">学院师生明细</span>
      </template>
      <el-table :data="collegeSummary" border empty-text="暂无学院统计数据">
        <el-table-column prop="collegeName" label="学院" min-width="160" />
        <el-table-column prop="studentCount" label="学生人数" width="120" />
        <el-table-column prop="teacherCount" label="教师人数" width="120" />
        <el-table-column label="学生占比" width="120">
          <template #default="{ row }">{{ percent(row.studentShare) }}</template>
        </el-table-column>
        <el-table-column label="教师占比" width="120">
          <template #default="{ row }">{{ percent(row.teacherShare) }}</template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import * as echarts from 'echarts'
import { nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import { getAdminDashboardData } from '../../data/mockData'

const loading = ref(false)
const stats = ref([])
const collegeSummary = ref([])
const studentPieRows = ref([])
const teacherPieRows = ref([])
const barRef = ref(null)
const studentPieRef = ref(null)
const teacherPieRef = ref(null)

let barChart = null
let studentPieChart = null
let teacherPieChart = null

onMounted(refresh)
onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  disposeCharts()
})

async function refresh() {
  loading.value = true
  try {
    const data = await getAdminDashboardData()
    stats.value = data.stats
    collegeSummary.value = data.collegeSummary
    studentPieRows.value = data.studentPieRows
    teacherPieRows.value = data.teacherPieRows
    await nextTick()
    renderCharts()
    window.addEventListener('resize', handleResize)
  } finally {
    loading.value = false
  }
}

function renderCharts() {
  if (!barRef.value || !studentPieRef.value || !teacherPieRef.value) {
    return
  }

  barChart = echarts.getInstanceByDom(barRef.value) || echarts.init(barRef.value)
  studentPieChart = echarts.getInstanceByDom(studentPieRef.value) || echarts.init(studentPieRef.value)
  teacherPieChart = echarts.getInstanceByDom(teacherPieRef.value) || echarts.init(teacherPieRef.value)

  barChart.setOption(buildBarOption())
  studentPieChart.setOption(buildPieOption('学生占比', studentPieRows.value, ['#2563eb', '#60a5fa', '#93c5fd']))
  teacherPieChart.setOption(buildPieOption('教师占比', teacherPieRows.value, ['#10b981', '#34d399', '#6ee7b7']))
}

function buildBarOption() {
  return {
    color: ['#2563eb', '#10b981'],
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    legend: { data: ['学生人数', '教师人数'] },
    grid: { left: 52, right: 24, top: 40, bottom: 52, containLabel: true },
    xAxis: {
      type: 'category',
      name: '学院',
      axisLabel: { interval: 0, rotate: 15 },
      data: collegeSummary.value.map((item) => item.collegeName)
    },
    yAxis: {
      type: 'value',
      name: '人数'
    },
    series: [
      {
        name: '学生人数',
        type: 'bar',
        barMaxWidth: 32,
        data: collegeSummary.value.map((item) => item.studentCount)
      },
      {
        name: '教师人数',
        type: 'bar',
        barMaxWidth: 32,
        data: collegeSummary.value.map((item) => item.teacherCount)
      }
    ]
  }
}

function buildPieOption(title, rows, colors) {
  return {
    color: colors,
    tooltip: { trigger: 'item' },
    legend: {
      bottom: 0,
      left: 'center'
    },
    series: [
      {
        name: title,
        type: 'pie',
        radius: ['42%', '70%'],
        center: ['50%', '42%'],
        avoidLabelOverlap: true,
        label: {
          formatter: '{b}\n{d}%'
        },
        data: rows
      }
    ]
  }
}

function percent(value) {
  return `${Math.round(Number(value || 0) * 100)}%`
}

function handleResize() {
  barChart?.resize()
  studentPieChart?.resize()
  teacherPieChart?.resize()
}

function disposeCharts() {
  barChart?.dispose()
  studentPieChart?.dispose()
  teacherPieChart?.dispose()
  barChart = null
  studentPieChart = null
  teacherPieChart = null
}
</script>
