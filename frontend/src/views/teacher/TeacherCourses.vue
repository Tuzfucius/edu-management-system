<template>
  <div class="page">
    <div>
      <h1 class="page-title">我的任课</h1>
      <div class="page-description">只展示当前教师自己的任课安排和选课学生规模。</div>
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

      <el-table :data="filteredTasks" border>
        <el-table-column prop="course" label="课程名称" />
        <el-table-column prop="semester" label="学期" width="140" />
        <el-table-column prop="time" label="上课时间" width="140" />
        <el-table-column prop="room" label="教室" width="130" />
        <el-table-column prop="selected" label="选课人数" width="100" />
        <el-table-column label="操作" width="160">
          <template #default>
            <el-button link type="primary">查看学生</el-button>
            <el-button link type="primary">录入成绩</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { teacherTasks } from '../../data/mockData'

const semester = ref('2025-2026-1')
const keyword = ref('')

const filteredTasks = computed(() => {
  return teacherTasks.filter((item) => !keyword.value || item.course.includes(keyword.value))
})
</script>
