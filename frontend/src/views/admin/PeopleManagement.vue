<template>
  <div class="page">
    <div>
      <h1 class="page-title">人员管理</h1>
      <div class="page-description">集中维护学生、教师和登录账号信息。</div>
    </div>

    <div class="content-grid">
      <el-card>
        <template #header>
          <div class="toolbar">
            <span class="section-title">学生列表</span>
            <el-button type="primary">新增学生</el-button>
          </div>
        </template>
        <el-table :data="students" border>
          <el-table-column prop="no" label="学号" width="120" />
          <el-table-column prop="name" label="姓名" width="100" />
          <el-table-column prop="major" label="专业" />
          <el-table-column prop="className" label="班级" width="120" />
          <el-table-column label="状态" width="90">
            <template #default="{ row }">
              <el-tag type="success" effect="plain">{{ row.status }}</el-tag>
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <el-card>
        <template #header>
          <div class="toolbar">
            <span class="section-title">教师列表</span>
            <el-button type="primary">新增教师</el-button>
          </div>
        </template>
        <el-table :data="teachers" border>
          <el-table-column prop="no" label="工号" width="110" />
          <el-table-column prop="name" label="姓名" width="90" />
          <el-table-column prop="department" label="教研室" />
          <el-table-column prop="title" label="职称" width="90" />
        </el-table>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { students, teachers } from '../../data/mockData'
</script>
