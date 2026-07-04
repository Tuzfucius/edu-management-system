<template>
  <div class="page">
    <div>
      <h1 class="page-title">我的成绩</h1>
      <div class="page-description">查看已选课程、学分和成绩状态。</div>
    </div>

    <div class="stat-grid">
      <el-card class="stat-card">
        <div class="stat-title">平均分</div>
        <div class="stat-number">{{ averageScore }}</div>
        <div class="stat-extra">仅统计已录入成绩</div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-title">已选课程</div>
        <div class="stat-number">{{ rows.length }}</div>
        <div class="stat-extra">当前有效选课</div>
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
      <el-table v-loading="loading" :data="rows" border empty-text="暂无成绩数据">
        <el-table-column prop="courseName" label="课程" />
        <el-table-column prop="teacherName" label="教师" width="110" />
        <el-table-column prop="credit" label="学分" width="80" />
        <el-table-column prop="semester" label="学期" width="130" />
        <el-table-column label="成绩" width="100">
          <template #default="{ row }">{{ row.score ?? '-' }}</template>
        </el-table-column>
        <el-table-column label="状态" width="110">
          <template #default="{ row }">
            <el-tag :type="row.gradeStatus === 1 ? 'success' : 'warning'" effect="plain">{{ row.gradeStatus === 1 ? gradeText(row.score) : '未录入' }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { getStudentByUser } from '../../api/student'
import { listStudentCourses } from '../../api/studentCourse'

const loading = ref(false)
const rows = ref([])
const currentUser = JSON.parse(localStorage.getItem('currentUser') || 'null')

const gradedRows = computed(() => rows.value.filter((item) => item.score !== null && item.score !== undefined))
const averageScore = computed(() => gradedRows.value.length ? (gradedRows.value.reduce((sum, item) => sum + Number(item.score), 0) / gradedRows.value.length).toFixed(1) : '-')
const earnedCredits = computed(() => gradedRows.value.filter((item) => Number(item.score) >= 60).reduce((sum, item) => sum + Number(item.credit || 0), 0))
const pendingCount = computed(() => rows.value.length - gradedRows.value.length)

onMounted(refresh)

async function refresh() {
  loading.value = true
  try {
    const student = await getStudentByUser(currentUser.id)
    rows.value = await listStudentCourses({ studentId: student.id })
  } finally {
    loading.value = false
  }
}

function gradeText(score) {
  if (score >= 90) return '优秀'
  if (score >= 80) return '良好'
  if (score >= 70) return '中等'
  if (score >= 60) return '及格'
  return '不及格'
}
</script>
