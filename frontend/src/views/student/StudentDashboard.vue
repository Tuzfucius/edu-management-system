<template>
  <div class="page" v-loading="loading">
    <div>
      <h1 class="page-title">学生工作台</h1>
      <div class="page-description">查看课程、成绩和个人培养进度。</div>
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

        <el-table v-loading="loading" :data="filteredCourses" border empty-text="暂无课程">
          <el-table-column prop="courseCode" label="课程编号" width="120" />
          <el-table-column prop="name" label="课程" />
          <el-table-column prop="teacherName" label="教师" width="110" />
          <el-table-column prop="semester" label="学期" width="140" />
          <el-table-column prop="time" label="时间" width="140" />
          <el-table-column prop="room" label="地点" width="130" />
          <el-table-column label="成绩" width="100">
            <template #default="{ row }">{{ row.score ?? '-' }}</template>
          </el-table-column>
          <el-table-column label="成绩状态" width="110">
            <template #default="{ row }">
              <el-tag :type="Number(row.gradeStatus) === 1 ? 'success' : 'warning'" effect="plain">
                {{ Number(row.gradeStatus) === 1 ? '已录入' : '未录入' }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <el-card>
        <template #header><span class="section-title">个人信息</span></template>
        <el-descriptions :column="1" border>
          <el-descriptions-item label="学号">{{ student?.studentNo || '-' }}</el-descriptions-item>
          <el-descriptions-item label="姓名">{{ student?.studentName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="班级">{{ student?.className || '-' }}</el-descriptions-item>
          <el-descriptions-item label="专业">{{ student?.majorName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="学院">{{ student?.collegeName || '-' }}</el-descriptions-item>
        </el-descriptions>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { getStudentDashboardData } from '../../data/mockData'
import { filterRows, pickPreferredValue, uniqueValues } from '../../utils/filter'

const loading = ref(false)
const stats = ref([])
const courses = ref([])
const student = ref(null)
const semester = ref('')
const gradeState = ref('全部')
const keyword = ref('')

const semesterOptions = computed(() => uniqueValues(courses.value, 'semester').sort().reverse())

const filteredCourses = computed(() =>
  filterRows(courses.value, {
    keyword: keyword.value,
    fields: ['courseCode', 'name', 'teacherName', 'semester'],
    predicates: [
      (row) => !semester.value || row.semester === semester.value,
      (row) => {
        if (gradeState.value === '全部') {
          return true
        }
        const graded = Number(row.gradeStatus) === 1
        return gradeState.value === '已录入' ? graded : !graded
      }
    ]
  })
)

onMounted(refresh)

async function refresh() {
  loading.value = true
  try {
    const data = await getStudentDashboardData()
    stats.value = data.stats
    courses.value = data.courses
    student.value = data.student
    semester.value = pickPreferredValue(semesterOptions.value, '2025-2026-1')
  } finally {
    loading.value = false
  }
}
</script>
