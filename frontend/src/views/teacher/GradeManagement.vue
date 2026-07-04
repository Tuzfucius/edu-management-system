<template>
  <div class="page">
    <div>
      <h1 class="page-title">成绩录入</h1>
      <div class="page-description">教师维护本人任课课程下学生成绩。</div>
    </div>

    <el-card>
      <div class="toolbar">
        <div class="toolbar-left">
          <el-input v-model="keyword" placeholder="搜索学号、姓名或课程" clearable style="width: 260px" />
        </div>
        <el-button type="primary" @click="saveGrades">批量保存</el-button>
      </div>

      <el-table v-loading="loading" :data="filteredRows" border>
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="studentName" label="姓名" width="100" />
        <el-table-column prop="courseName" label="课程" />
        <el-table-column label="成绩" width="160">
          <template #default="{ row }">
            <el-input-number v-model="row.score" :min="0" :max="100" controls-position="right" />
          </template>
        </el-table-column>
        <el-table-column label="状态" width="110">
          <template #default="{ row }">
            <el-tag :type="row.gradeStatus === 1 ? 'success' : 'warning'" effect="plain">
              {{ row.gradeStatus === 1 ? '已录入' : '未录入' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { listStudentCourses, updateScore } from '../../api/studentCourse'
import { getTeacherByUser } from '../../api/teacher'

const keyword = ref('')
const loading = ref(false)
const rows = ref([])
const currentUser = JSON.parse(localStorage.getItem('currentUser') || 'null')

const filteredRows = computed(() => rows.value.filter((item) => {
  return !keyword.value || [item.studentNo, item.studentName, item.courseName].some((value) => String(value || '').includes(keyword.value))
}))

onMounted(refresh)

async function refresh() {
  loading.value = true
  try {
    const teacher = await getTeacherByUser(currentUser.id)
    rows.value = await listStudentCourses({ teacherId: teacher.id })
  } finally {
    loading.value = false
  }
}

async function saveGrades() {
  await Promise.all(rows.value.filter((row) => row.score !== null && row.score !== undefined).map((row) => updateScore(row.id, { score: row.score, remark: row.remark })))
  ElMessage.success('成绩已保存')
  await refresh()
}
</script>
