<template>
  <div class="page">
    <div>
      <h1 class="page-title">成绩录入</h1>
      <div class="page-description">教师维护本人任课课程下学生成绩，当前为静态演示数据。</div>
    </div>

    <el-card>
      <div class="toolbar">
        <div class="toolbar-left">
          <el-select v-model="course" style="width: 180px">
            <el-option label="数据库原理" value="数据库原理" />
            <el-option label="Java Web 开发" value="Java Web 开发" />
          </el-select>
          <el-input v-model="keyword" placeholder="搜索学号或姓名" clearable style="width: 220px" />
        </div>
        <el-button type="primary" @click="saveGrades">批量保存</el-button>
      </div>

      <el-table :data="filteredRows" border>
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="studentName" label="姓名" width="100" />
        <el-table-column prop="course" label="课程" />
        <el-table-column label="成绩" width="160">
          <template #default="{ row }">
            <el-input-number v-model="row.score" :min="0" :max="100" :step="1" controls-position="right" />
          </template>
        </el-table-column>
        <el-table-column label="状态" width="110">
          <template #default="{ row }">
            <el-tag :type="row.score === null ? 'warning' : 'success'" effect="plain">
              {{ row.score === null ? '未录入' : '已录入' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { gradeRows } from '../../data/mockData'

const course = ref('数据库原理')
const keyword = ref('')

const filteredRows = computed(() => {
  return gradeRows.filter((item) => {
    const matchCourse = item.course === course.value
    const matchKeyword = !keyword.value || item.studentNo.includes(keyword.value) || item.studentName.includes(keyword.value)
    return matchCourse && matchKeyword
  })
})

function saveGrades() {
  ElMessage.success('成绩已暂存到前端演示数据')
}
</script>
