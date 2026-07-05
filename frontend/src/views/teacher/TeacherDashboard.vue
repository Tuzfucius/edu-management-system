<template>
  <div class="page" v-loading="loading">
    <div>
      <h1 class="page-title">教师工作台</h1>
      <div class="page-description">查看本学期任课、学生规模和成绩录入进度。</div>
    </div>

    <div class="stat-grid">
      <el-card v-for="item in stats" :key="item.title" class="stat-card">
        <div class="stat-title">{{ item.title }}</div>
        <div class="stat-number">{{ item.value }}</div>
        <div class="stat-extra">{{ item.extra }}</div>
      </el-card>
    </div>

    <div class="content-grid">
      <el-card>
        <template #header><span class="section-title">本周课程</span></template>

        <div class="toolbar">
          <div class="toolbar-left">
            <el-select v-model="semester" clearable placeholder="学期" style="width: 160px">
              <el-option v-for="item in semesterOptions" :key="item" :label="item" :value="item" />
            </el-select>
            <el-select v-model="taskState" clearable placeholder="任务状态" style="width: 140px">
              <el-option label="全部" value="全部" />
              <el-option label="进行中" value="进行中" />
              <el-option label="已结课" value="已结课" />
            </el-select>
            <el-input v-model="keyword" placeholder="搜索课程名称" clearable style="width: 220px" />
          </div>
        </div>

        <el-table :data="displayTasks" border empty-text="暂无任课安排">
          <el-table-column prop="course" label="课程" />
          <el-table-column prop="semester" label="学期" width="140" />
          <el-table-column label="上课时间" width="160">
            <template #default="{ row }">{{ row.time }}</template>
          </el-table-column>
          <el-table-column prop="room" label="教室" width="120" />
          <el-table-column prop="selected" label="学生数" width="90" />
          <el-table-column label="状态" width="110">
            <template #default="{ row }">
              <el-tag :type="Number(row.taskStatus) === 1 ? 'success' : 'info'" effect="plain">
                {{ Number(row.taskStatus) === 1 ? '进行中' : '已结课' }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <el-card>
        <template #header><span class="section-title">成绩录入进度</span></template>
        <el-space direction="vertical" fill style="width: 100%">
          <div v-for="task in displayTasks" :key="task.id">
            <div class="toolbar">
              <span>{{ task.course }}</span>
              <span class="muted">待录 {{ task.pending }} 人</span>
            </div>
            <el-progress :percentage="progress(task)" />
          </div>
        </el-space>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { getTeacherDashboardData } from '../../data/mockData'
import { filterRows, pickPreferredValue, uniqueValues } from '../../utils/filter'

const loading = ref(false)
const stats = ref([])
const tasks = ref([])
const semester = ref('2025-2026-1')
const taskState = ref('全部')
const keyword = ref('')

const semesterOptions = computed(() => uniqueValues(tasks.value, 'semester').sort().reverse())

const displayTasks = computed(() =>
  filterRows(tasks.value, {
    keyword: keyword.value,
    fields: ['course', 'semester', 'teacherName'],
    predicates: [
      (row) => !semester.value || row.semester === semester.value,
      (row) => {
        if (taskState.value === '全部') {
          return true
        }
        const active = Number(row.taskStatus) === 1
        return taskState.value === '进行中' ? active : !active
      }
    ]
  })
)

onMounted(refresh)

async function refresh() {
  loading.value = true
  try {
    const data = await getTeacherDashboardData()
    stats.value = data.stats
    tasks.value = data.tasks
    semester.value = pickPreferredValue(semesterOptions.value, '2025-2026-1') || semester.value
  } finally {
    loading.value = false
  }
}

function progress(task) {
  if (!task.selected) return 0
  return Math.round(((task.selected - task.pending) / task.selected) * 100)
}
</script>
