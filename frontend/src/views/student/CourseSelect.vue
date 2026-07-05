<template>
  <div class="page">
    <div>
      <h1 class="page-title">可选课程</h1>
      <div class="page-description">查看本学期可选课程，完成选课或退课。</div>
    </div>

    <el-card>
      <div class="toolbar">
        <div class="toolbar-left">
          <el-select v-model="semester" style="width: 160px" @change="refresh">
            <el-option label="2025-2026-1" value="2025-2026-1" />
            <el-option label="2025-2026-2" value="2025-2026-2" />
          </el-select>
          <el-select v-model="courseState" clearable placeholder="课程状态" style="width: 140px">
            <el-option label="全部" value="全部" />
            <el-option label="可选" value="可选" />
            <el-option label="已选" value="已选" />
            <el-option label="已满" value="已满" />
          </el-select>
          <el-input v-model="keyword" placeholder="搜索课程或教师" clearable style="width: 240px" />
        </div>
      </div>

      <el-table v-loading="loading" :data="filteredCourses" border empty-text="暂无可选课程">
        <el-table-column prop="courseCode" label="课程编号" width="120" />
        <el-table-column prop="courseName" label="课程名称" />
        <el-table-column prop="teacherName" label="教师" width="100" />
        <el-table-column label="时间" width="140">
          <template #default="{ row }">{{ formatTime(row) }}</template>
        </el-table-column>
        <el-table-column prop="credit" label="学分" width="80" />
        <el-table-column label="容量" width="130">
          <template #default="{ row }">{{ row.selectedCount }} / {{ row.capacity }}</template>
        </el-table-column>
        <el-table-column label="课程状态" width="110">
          <template #default="{ row }">
            <el-tag :type="courseStatusType(row)" effect="plain">{{ courseStatusText(row) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="130">
          <template #default="{ row }">
            <el-button v-if="Number(row.selectedByMe) === 1" type="warning" size="small" @click="drop(row)">退课</el-button>
            <el-button v-else :disabled="row.selectedCount >= row.capacity" type="primary" size="small" @click="select(row)">
              {{ row.selectedCount >= row.capacity ? '已满' : '选课' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getStudentByUser } from '../../api/student'
import { dropCourse, listSelectableCourses, selectCourse } from '../../api/studentCourse'
import { filterRows } from '../../utils/filter'

const semester = ref('2025-2026-1')
const keyword = ref('')
const courseState = ref('全部')
const loading = ref(false)
const student = ref(null)
const courses = ref([])
const currentUser = JSON.parse(localStorage.getItem('currentUser') || 'null')

const filteredCourses = computed(() =>
  filterRows(courses.value, {
    keyword: keyword.value,
    fields: ['courseCode', 'courseName', 'teacherName'],
    predicates: [
      (row) => {
        if (courseState.value === '全部') {
          return true
        }
        if (courseState.value === '已选') {
          return Number(row.selectedByMe) === 1
        }
        if (courseState.value === '已满') {
          return Number(row.selectedCount) >= Number(row.capacity)
        }
        return Number(row.selectedByMe) !== 1 && Number(row.selectedCount) < Number(row.capacity)
      }
    ]
  })
)

onMounted(refresh)

async function refresh() {
  loading.value = true
  try {
    if (!student.value) {
      student.value = await getStudentByUser(currentUser.id)
    }
    courses.value = await listSelectableCourses({ studentId: student.value.id, semester: semester.value })
  } finally {
    loading.value = false
  }
}

async function select(row) {
  await selectCourse({ studentId: student.value.id, teachingTaskId: row.id })
  ElMessage.success('选课成功')
  await refresh()
}

async function drop(row) {
  await dropCourse(row.studentCourseId)
  ElMessage.success('退课成功')
  await refresh()
}

function formatTime(row) {
  return `周${['一', '二', '三', '四', '五', '六', '日'][Number(row.weekday) - 1] || row.weekday} ${row.startSection}-${row.endSection}节`
}

function courseStatusText(row) {
  if (Number(row.selectedByMe) === 1) {
    return '已选'
  }
  if (Number(row.selectedCount) >= Number(row.capacity)) {
    return '已满'
  }
  return '可选'
}

function courseStatusType(row) {
  if (Number(row.selectedByMe) === 1) {
    return 'success'
  }
  if (Number(row.selectedCount) >= Number(row.capacity)) {
    return 'danger'
  }
  return 'info'
}
</script>
