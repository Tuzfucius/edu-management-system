<template>
  <div class="page">
    <div>
      <h1 class="page-title">我的任课</h1>
      <div class="page-description">展示当前教师的任课安排、容量和上课时间。</div>
    </div>

    <el-card>
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

      <el-table v-loading="loading" :data="displayTasks" border empty-text="暂无任课安排">
        <el-table-column prop="courseName" label="课程名称" />
        <el-table-column prop="semester" label="学期" width="140" />
        <el-table-column label="上课时间" width="160">
          <template #default="{ row }">{{ formatTime(row) }}</template>
        </el-table-column>
        <el-table-column prop="classroom" label="教室" width="130" />
        <el-table-column label="容量" width="120">
          <template #default="{ row }">{{ row.selectedCount }} / {{ row.capacity }}</template>
        </el-table-column>
        <el-table-column label="状态" width="110">
          <template #default="{ row }">
            <el-tag :type="Number(row.taskStatus) === 1 ? 'success' : 'info'" effect="plain">
              {{ Number(row.taskStatus) === 1 ? '进行中' : '已结课' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { getTeacherByUser } from '../../api/teacher'
import { listTeachingTasks } from '../../api/teachingTask'
import { filterRows, pickPreferredValue, uniqueValues } from '../../utils/filter'

const semester = ref('2025-2026-1')
const taskState = ref('全部')
const keyword = ref('')
const loading = ref(false)
const tasks = ref([])
const currentUser = JSON.parse(localStorage.getItem('currentUser') || 'null')

const semesterOptions = computed(() => uniqueValues(tasks.value, 'semester').sort().reverse())

const displayTasks = computed(() =>
  filterRows(tasks.value, {
    keyword: keyword.value,
    fields: ['courseName', 'semester', 'classroom'],
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
    const teacher = await getTeacherByUser(currentUser.id)
    tasks.value = await listTeachingTasks({ teacherId: teacher.id })
    semester.value = pickPreferredValue(semesterOptions.value, '2025-2026-1') || semester.value
  } finally {
    loading.value = false
  }
}

function formatTime(row) {
  return `周${['一', '二', '三', '四', '五', '六', '日'][Number(row.weekday) - 1] || row.weekday} ${row.startSection}-${row.endSection}节`
}
</script>
