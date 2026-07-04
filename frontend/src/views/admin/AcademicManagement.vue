<template>
  <div class="page">
    <div>
      <h1 class="page-title">组织管理</h1>
      <div class="page-description">维护学院、专业、教研室和班级等基础组织数据。</div>
    </div>

    <el-card>
      <div class="toolbar">
        <div class="toolbar-left">
          <el-input v-model="keyword" placeholder="搜索学院编号或名称" clearable style="width: 240px" />
          <el-select v-model="status" placeholder="状态" style="width: 128px">
            <el-option label="全部" value="全部" />
            <el-option label="正常" value="正常" />
            <el-option label="停用" value="停用" />
          </el-select>
        </div>
        <el-button type="primary">新增学院</el-button>
      </div>

      <el-table :data="filteredColleges" border>
        <el-table-column prop="code" label="学院编号" width="120" />
        <el-table-column prop="name" label="学院名称" />
        <el-table-column prop="majors" label="专业数" width="100" />
        <el-table-column prop="classes" label="班级数" width="100" />
        <el-table-column prop="students" label="学生数" width="100" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag type="success" effect="plain">{{ row.status }}</el-tag>
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

    <el-row :gutter="16">
      <el-col :xs="24" :md="8">
        <el-card>
          <template #header><span class="section-title">专业管理</span></template>
          <el-space direction="vertical" alignment="start" fill>
            <span class="muted">按学院筛选专业，维护学制和学位类型。</span>
            <el-button type="primary" plain>进入专业列表</el-button>
          </el-space>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="8">
        <el-card>
          <template #header><span class="section-title">教研室管理</span></template>
          <el-space direction="vertical" alignment="start" fill>
            <span class="muted">维护教研室编号、所属学院和办公地点。</span>
            <el-button type="primary" plain>进入教研室列表</el-button>
          </el-space>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="8">
        <el-card>
          <template #header><span class="section-title">班级管理</span></template>
          <el-space direction="vertical" alignment="start" fill>
            <span class="muted">维护班级、所属专业和入学年份。</span>
            <el-button type="primary" plain>进入班级列表</el-button>
          </el-space>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { colleges } from '../../data/mockData'

const keyword = ref('')
const status = ref('全部')

const filteredColleges = computed(() => {
  return colleges.filter((item) => {
    const matchKeyword = !keyword.value || item.code.includes(keyword.value) || item.name.includes(keyword.value)
    const matchStatus = status.value === '全部' || item.status === status.value
    return matchKeyword && matchStatus
  })
})
</script>
