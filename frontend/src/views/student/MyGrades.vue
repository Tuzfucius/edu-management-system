<template>
  <div class="page">
    <div>
      <h1 class="page-title">我的成绩</h1>
      <div class="page-description">按学期查看课程成绩、学分和成绩状态。</div>
    </div>

    <div class="stat-grid">
      <el-card class="stat-card">
        <div class="stat-title">平均分</div>
        <div class="stat-number">87.3</div>
        <div class="stat-extra">仅统计已录入成绩</div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-title">已获学分</div>
        <div class="stat-number">10.5</div>
        <div class="stat-extra">本学期累计</div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-title">最高分</div>
        <div class="stat-number">92</div>
        <div class="stat-extra">程序设计基础</div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-title">待出成绩</div>
        <div class="stat-number">1</div>
        <div class="stat-extra">数据库原理</div>
      </el-card>
    </div>

    <el-card>
      <div class="toolbar">
        <div class="toolbar-left">
          <el-select v-model="semester" style="width: 160px">
            <el-option label="2025-2026-1" value="2025-2026-1" />
            <el-option label="2025-2026-2" value="2025-2026-2" />
          </el-select>
        </div>
      </div>
      <el-table :data="studentGrades" border>
        <el-table-column prop="course" label="课程" />
        <el-table-column prop="teacher" label="教师" width="100" />
        <el-table-column prop="credit" label="学分" width="80" />
        <el-table-column label="成绩" width="100">
          <template #default="{ row }">{{ row.score ?? '-' }}</template>
        </el-table-column>
        <el-table-column label="等级" width="110">
          <template #default="{ row }">
            <el-tag :type="row.score === null ? 'warning' : 'success'" effect="plain">{{ row.grade }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { studentGrades } from '../../data/mockData'

const semester = ref('2025-2026-1')
</script>
