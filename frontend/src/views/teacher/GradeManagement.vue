<template>
  <div class="page">
    <div>
      <h1 class="page-title">成绩录入</h1>
      <div class="page-description">先选择任课课程，再维护该课程学生成绩。</div>
    </div>

    <el-card v-if="!selectedTask">
      <div class="toolbar">
        <div class="toolbar-left">
          <el-select v-model="semester" clearable placeholder="学期" style="width: 180px">
            <el-option v-for="item in semesterOptions" :key="item" :label="item" :value="item" />
          </el-select>
          <el-input v-model="keyword" placeholder="搜索课程、教室或学期" clearable style="width: 260px" />
        </div>
      </div>

      <el-table v-loading="loading" :data="taskRows" border empty-text="暂无任课课程">
        <el-table-column prop="courseCode" label="课程编号" width="120" />
        <el-table-column prop="courseName" label="课程" />
        <el-table-column prop="semester" label="学期" width="130" />
        <el-table-column label="时间" width="160">
          <template #default="{ row }">{{ formatTime(row) }}</template>
        </el-table-column>
        <el-table-column prop="classroom" label="教室" width="110" />
        <el-table-column label="人数" width="100">
          <template #default="{ row }">{{ courseStudentCount(row.id) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="enterTask(row)">录入成绩</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <div v-else class="grade-workspace">
      <el-card class="summary-panel">
        <template #header>
          <div class="card-header">
            <span>{{ selectedTask.courseName }}</span>
            <el-button link type="primary" @click="leaveTask">返回课程</el-button>
          </div>
        </template>
        <div class="metric-list">
          <div>
            <span class="muted">平均分</span>
            <strong>{{ average }}</strong>
          </div>
          <div>
            <span class="muted">中位数</span>
            <strong>{{ median }}</strong>
          </div>
          <div>
            <span class="muted">已录/总人数</span>
            <strong>{{ gradedRows.length }} / {{ selectedRows.length }}</strong>
          </div>
        </div>
        <div ref="barRef" class="chart chart--small"></div>
        <div ref="ringRef" class="chart chart--small"></div>
      </el-card>

      <el-card class="student-panel">
        <template #header>
          <div class="card-header">
            <span>学生名单</span>
            <el-button type="primary" @click="saveAll">批量保存</el-button>
          </div>
        </template>
        <el-table :data="selectedRows" border empty-text="暂无学生">
          <el-table-column prop="studentNo" label="学号" width="120" />
          <el-table-column prop="studentName" label="姓名" width="110" />
          <el-table-column label="成绩" width="170">
            <template #default="{ row }">
              <el-input-number v-model="row.score" :min="0" :max="100" controls-position="right" />
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="备注">
            <template #default="{ row }">
              <el-input v-model="row.remark" clearable />
            </template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="Number(row.gradeStatus) === 1 ? 'success' : 'warning'" effect="plain">
                {{ Number(row.gradeStatus) === 1 ? '已录入' : '未录入' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150">
            <template #default="{ row }">
              <el-button link type="primary" @click="saveRow(row)">保存</el-button>
              <el-button link type="danger" :disabled="Number(row.gradeStatus) !== 1" @click="revokeRow(row)">撤销</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import * as echarts from 'echarts'
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { getTeacherByUser } from '../../api/teacher'
import { listStudentCourses, revokeScore, updateScore } from '../../api/studentCourse'
import { listTeachingTasks } from '../../api/teachingTask'
import { averageScore, buildLevelRows, buildScoreBuckets, BUCKET_COLORS, getCurrentSemester, medianScore } from '../../utils/academicMetrics'
import { filterRows, pickPreferredValue, uniqueValues } from '../../utils/filter'

const currentUser = JSON.parse(localStorage.getItem('currentUser') || 'null')
const loading = ref(false)
const keyword = ref('')
const semester = ref(getCurrentSemester())
const tasks = ref([])
const rows = ref([])
const selectedTask = ref(null)
const barRef = ref()
const ringRef = ref()
let barChart = null
let ringChart = null

const semesterOptions = computed(() => uniqueValues(tasks.value, 'semester').sort().reverse())
const taskRows = computed(() =>
  filterRows(tasks.value, {
    keyword: keyword.value,
    fields: ['courseCode', 'courseName', 'classroom', 'semester'],
    predicates: [(row) => !semester.value || row.semester === semester.value]
  })
)
const selectedRows = computed(() => rows.value.filter((row) => String(row.teachingTaskId) === String(selectedTask.value?.id)))
const gradedRows = computed(() => selectedRows.value.filter((row) => Number(row.gradeStatus) === 1 && row.score !== null && row.score !== undefined))
const average = computed(() => averageScore(gradedRows.value))
const median = computed(() => medianScore(gradedRows.value))

watch([selectedTask, gradedRows], () => nextTick(renderCharts), { deep: true })

onMounted(refresh)
onBeforeUnmount(disposeCharts)

async function refresh() {
  loading.value = true
  try {
    const teacher = await getTeacherByUser(currentUser.id)
    ;[tasks.value, rows.value] = await Promise.all([
      listTeachingTasks({ teacherId: teacher.id }),
      listStudentCourses({ teacherId: teacher.id })
    ])
    semester.value = pickPreferredValue(semesterOptions.value, getCurrentSemester()) || semester.value
  } finally {
    loading.value = false
  }
}

function enterTask(row) {
  selectedTask.value = row
}

function leaveTask() {
  selectedTask.value = null
  disposeCharts()
}

async function saveRow(row) {
  if (row.score === null || row.score === undefined) {
    ElMessage.warning('请先输入成绩')
    return
  }
  await updateScore(row.id, { score: row.score, remark: row.remark })
  ElMessage.success('成绩已保存')
  await refreshKeepTask()
}

async function saveAll() {
  const changedRows = selectedRows.value.filter((row) => row.score !== null && row.score !== undefined)
  await Promise.all(changedRows.map((row) => updateScore(row.id, { score: row.score, remark: row.remark })))
  ElMessage.success('成绩已批量保存')
  await refreshKeepTask()
}

async function revokeRow(row) {
  await revokeScore(row.id)
  ElMessage.success('成绩已撤销')
  await refreshKeepTask()
}

async function refreshKeepTask() {
  const taskId = selectedTask.value?.id
  await refresh()
  selectedTask.value = tasks.value.find((item) => String(item.id) === String(taskId)) || null
}

function renderCharts() {
  if (!selectedTask.value || !barRef.value || !ringRef.value) return
  barChart = echarts.getInstanceByDom(barRef.value) || echarts.init(barRef.value)
  ringChart = echarts.getInstanceByDom(ringRef.value) || echarts.init(ringRef.value)
  const buckets = buildScoreBuckets(gradedRows.value)
  barChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 36, right: 16, top: 24, bottom: 32, containLabel: true },
    xAxis: { type: 'category', data: buckets.map((item) => item.name) },
    yAxis: { type: 'value' },
    series: [{ type: 'bar', data: buckets.map((item, index) => ({ value: item.value, itemStyle: { color: BUCKET_COLORS[index] } })) }]
  })
  ringChart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: 0 },
    series: [{ type: 'pie', radius: ['46%', '70%'], data: buildLevelRows(gradedRows.value) }]
  })
}

function disposeCharts() {
  barChart?.dispose()
  ringChart?.dispose()
  barChart = null
  ringChart = null
}

function courseStudentCount(taskId) {
  return rows.value.filter((row) => String(row.teachingTaskId) === String(taskId)).length
}

function formatTime(row) {
  return `周${['一', '二', '三', '四', '五', '六', '日'][Number(row.weekday) - 1] || row.weekday} ${row.startSection}-${row.endSection}节`
}
</script>

<style scoped>
.grade-workspace {
  display: grid;
  grid-template-columns: minmax(280px, 360px) minmax(0, 1fr);
  gap: 16px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.metric-list {
  display: grid;
  gap: 10px;
  margin-bottom: 12px;
}

.metric-list > div {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

@media (max-width: 1100px) {
  .grade-workspace {
    grid-template-columns: 1fr;
  }
}
</style>
