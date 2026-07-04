<template>
  <div class="page">
    <div>
      <h1 class="page-title">我的任课</h1>
      <div class="page-description">展示当前教师的任课安排、容量和上课时间。</div>
    </div>

    <el-card>
      <div class="toolbar">
        <div class="toolbar-left">
          <el-select v-model="semester" style="width: 160px">
            <el-option label="2025-2026-1" value="2025-2026-1" />
            <el-option label="2025-2026-2" value="2025-2026-2" />
          </el-select>
          <el-input v-model="keyword" placeholder="搜索课程名称" clearable style="width: 220px" />
        </div>
      </div>

      <el-table v-loading="loading" :data="filteredTasks" border>
        <el-table-column prop="courseName" label="课程名称" />
        <el-table-column prop="semester" label="学期" width="140" />
        <el-table-column label="上课时间" width="160">
          <template #default="{ row }">{{ formatTime(row) }}</template>
        </el-table-column>
        <el-table-column prop="classroom" label="教室" width="130" />
        <el-table-column label="容量" width="120">
          <template #default="{ row }">{{ row.selectedCount }} / {{ row.capacity }}</template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { getTeacherByUser } from '../../api/teacher'
import { listTeachingTasks } from '../../api/teachingTask'

const semester = ref('2025-2026-1')
const keyword = ref('')
const loading = ref(false)
const tasks = ref([])
const currentUser = JSON.parse(localStorage.getItem('currentUser') || 'null')

const filteredTasks = computed(() => tasks.value.filter((item) => {
  return item.semester === semester.value && (!keyword.value || item.courseName.includes(keyword.value))
}))

onMounted(refresh)

async function refresh() {
  loading.value = true
  try {
    const teacher = await getTeacherByUser(currentUser.id)
    tasks.value = await listTeachingTasks({ teacherId: teacher.id })
  } finally {
    loading.value = false
  }
}

function formatTime(row) {
  return `周${['一', '二', '三', '四', '五', '六', '日'][Number(row.weekday) - 1] || row.weekday} ${row.startSection}-${row.endSection}节`
}
</script>
