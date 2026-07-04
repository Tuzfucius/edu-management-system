<template>
  <div>
    <el-row :gutter="16">
      <el-col :span="6">
        <el-card>
          <div class="stat-title">学生总数</div>
          <div class="stat-number">1280</div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card>
          <div class="stat-title">教师总数</div>
          <div class="stat-number">86</div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card>
          <div class="stat-title">课程总数</div>
          <div class="stat-number">142</div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card>
          <div class="stat-title">选课记录</div>
          <div class="stat-number">3560</div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="todo-card">
      <template #header>
        <span>系统建设进度</span>
      </template>

      <el-timeline>
        <el-timeline-item timestamp="当前阶段" type="success">
          Vue + Element Plus 前端骨架搭建
        </el-timeline-item>
        <el-timeline-item timestamp="下一阶段">
          学生管理页面与后端 API 联调
        </el-timeline-item>
        <el-timeline-item timestamp="后续阶段">
          课程、选课、成绩、统计报表开发
        </el-timeline-item>
      </el-timeline>
    </el-card>
  </div>
</template>

<style scoped>
.stat-title {
  color: #6b7280;
  font-size: 14px;
}

.stat-number {
  margin-top: 12px;
  font-size: 28px;
  font-weight: bold;
}

.todo-card {
  margin-top: 20px;
}
</style>