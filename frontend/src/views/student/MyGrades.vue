<template>
  <div class="page">
    <div>
      <h1 class="page-title">我的成绩</h1>
      <div class="page-description">查看已选课程、学分、成绩状态和成绩分布。</div>
    </div>

    <div class="stat-grid">
      <el-card class="stat-card">
        <div class="stat-title">五分制 GPA</div>
        <div class="stat-number">{{ gpa }}</div>
        <div class="stat-extra">按已录入成绩和学分加权</div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-title">平均分</div>
        <div class="stat-number">{{ average }}</div>
        <div class="stat-extra">当前筛选范围</div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-title">已获学分</div>
        <div class="stat-number">{{ earnedCredits }}</div>
        <div class="stat-extra">成绩合格课程</div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-title">待出成绩</div>
        <div class="stat-number">{{ pendingCount }}</div>
        <div class="stat-extra">未录入成绩</div>
      </el-card>
    </div>

    <el-card>
      <template #header><span class="section-title">成绩分布</span></template>
      <div ref="gradeChartRef" class="chart chart--wide"></div>
    </el-card>

    <el-card>
      <div class="toolbar">
        <div class="toolbar-left">
          <el-select v-model="semester" clearable placeholder="学期" style="width: 160px">
            <el-option v-for="item in semesterOptions" :key="item" :label="item" :value="item" />
          </el-select>
          <el-select v-model="gradeState" clearable placeholder="成绩状态" style="width: 140px">
            <el-option label="全部" value="全部" />
            <el-option label="已录入" value="已录入" />
            <el-option label="未录入" value="未录入" />
          </el-select>
          <el-input v-model="keyword" placeholder="搜索课程、教师或课程编号" clearable style="width: 240px" />
        </div>
      </div>

      <el-table v-loading="loading" :data="displayRows" border empty-text="暂无成绩数据">
        <el-table-column prop="courseCode" label="课程编号" width="120" />
        <el-table-column prop="courseName" label="课程" />
        <el-table-column prop="teacherName" label="教师" width="110" />
        <el-table-column prop="credit" label="学分" width="80" />
        <el-table-column prop="semester" label="学期" width="130" />
        <el-table-column label="成绩" width="100">
          <template #default="{ row }">{{ row.score ?? '-' }}</template>
        </el-table-column>
        <el-table-column label="水平" width="110">
          <template #default="{ row }">
            <el-tag :type="Number(row.gradeStatus) === 1 ? gradeTagType(row.score) : 'warning'" effect="plain">
              {{ Number(row.gradeStatus) === 1 ? gradeText(row.score) : '未录入' }}
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
import { getStudentByUser } from '../../api/student'
import { listStudentCourses } from '../../api/studentCourse'
import { averageScore, buildScoreBuckets, BUCKET_COLORS, fivePointGpa, getCurrentSemester, scoreLevel } from '../../utils/academicMetrics'
import { filterRows, pickPreferredValue, uniqueValues } from '../../utils/filter'

const loading = ref(false)
const rows = ref([])
const currentUser = JSON.parse(localStorage.getItem('currentUser') || 'null')
const semester = ref(getCurrentSemester())
const gradeState = ref('全部')
const keyword = ref('')
const gradeChartRef = ref()
let gradeChart = null

const semesterOptions = computed(() => uniqueValues(rows.value, 'semester').sort().reverse())
const displayRows = computed(() =>
  filterRows(rows.value, {
    keyword: keyword.value,
    fields: ['courseCode', 'courseName', 'teacherName', 'semester'],
    predicates: [
      (row) => !semester.value || row.semester === semester.value,
      (row) => {
        if (gradeState.value === '全部') return true
        const graded = Number(row.gradeStatus) === 1
        return gradeState.value === '已录入' ? graded : !graded
      }
    ]
  })
)
const gradedRows = computed(() => displayRows.value.filter((item) => Number(item.gradeStatus) === 1 && item.score !== null && item.score !== undefined))
const average = computed(() => averageScore(gradedRows.value))
const gpa = computed(() => fivePointGpa(displayRows.value))
const earnedCredits = computed(() =>
  gradedRows.value
    .filter((item) => Number(item.score) >= 60)
    .reduce((sum, item) => sum + Number(item.credit || 0), 0)
    .toFixed(1)
)
const pendingCount = computed(() => displayRows.value.length - gradedRows.value.length)

watch([displayRows, gradedRows], () => nextTick(renderChart), { deep: true })
onMounted(refresh)
onBeforeUnmount(disposeChart)

async function refresh() {
  loading.value = true
  try {
    const student = await getStudentByUser(currentUser.id)
    rows.value = await listStudentCourses({ studentId: student.id })
    semester.value = pickPreferredValue(semesterOptions.value, getCurrentSemester()) || ''
    await nextTick()
    renderChart()
  } finally {
    loading.value = false
  }
}

function renderChart() {
  if (!gradeChartRef.value) return
  gradeChart = echarts.getInstanceByDom(gradeChartRef.value) || echarts.init(gradeChartRef.value)
  const buckets = buildScoreBuckets(gradedRows.value)
  gradeChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 40, right: 20, top: 32, bottom: 36, containLabel: true },
    xAxis: { type: 'category', data: buckets.map((item) => item.name) },
    yAxis: { type: 'value', name: '课程数' },
    series: [{ type: 'bar', data: buckets.map((item, index) => ({ value: item.value, itemStyle: { color: BUCKET_COLORS[index] } })) }]
  })
}

function disposeChart() {
  gradeChart?.dispose()
  gradeChart = null
}

function gradeText(score) {
  return scoreLevel(score)
}

function gradeTagType(score) {
  const level = scoreLevel(score)
  if (level === '优秀') return 'success'
  if (level === '良好') return 'primary'
  if (level === '及格') return 'warning'
  return 'danger'
}
</script>
