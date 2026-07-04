<template>
  <div class="page">
    <div>
      <h1 class="page-title">课程管理</h1>
      <div class="page-description">维护课程基础信息和本学期任课安排。</div>
    </div>

    <el-card>
      <div class="toolbar">
        <div class="toolbar-left">
          <el-input v-model="keyword" placeholder="搜索课程编号或名称" clearable style="width: 240px" />
          <el-select v-model="semester" style="width: 160px">
            <el-option label="2025-2026-1" value="2025-2026-1" />
            <el-option label="2025-2026-2" value="2025-2026-2" />
          </el-select>
        </div>
        <el-button type="primary">新增课程</el-button>
      </div>

      <el-table :data="filteredCourses" border>
        <el-table-column prop="code" label="课程编号" width="120" />
        <el-table-column prop="name" label="课程名称" />
        <el-table-column prop="credit" label="学分" width="90" />
        <el-table-column prop="teacher" label="任课教师" width="110" />
        <el-table-column label="容量" width="150">
          <template #default="{ row }">
            {{ row.selected }} / {{ row.capacity }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.selected >= row.capacity ? 'danger' : 'success'" effect="plain">
              {{ row.selected >= row.capacity ? '已满' : '可选' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160">
          <template #default>
            <el-button link type="primary">编辑</el-button>
            <el-button link type="danger">停用</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { courses } from '../../data/mockData'

const keyword = ref('')
const semester = ref('2025-2026-1')

const filteredCourses = computed(() => {
  return courses.filter((item) => {
    return !keyword.value || item.code.includes(keyword.value) || item.name.includes(keyword.value)
  })
})
</script>
