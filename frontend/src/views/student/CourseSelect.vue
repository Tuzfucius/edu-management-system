<template>
  <div class="page">
    <div>
      <h1 class="page-title">可选课程</h1>
      <div class="page-description">查看本学期可选课程，完成选课或查看容量状态。</div>
    </div>

    <el-card>
      <div class="toolbar">
        <div class="toolbar-left">
          <el-select v-model="semester" style="width: 160px">
            <el-option label="2025-2026-1" value="2025-2026-1" />
            <el-option label="2025-2026-2" value="2025-2026-2" />
          </el-select>
          <el-input v-model="keyword" placeholder="搜索课程或教师" clearable style="width: 240px" />
        </div>
      </div>

      <el-table :data="filteredCourses" border>
        <el-table-column prop="code" label="课程编号" width="120" />
        <el-table-column prop="name" label="课程名称" />
        <el-table-column prop="teacher" label="教师" width="100" />
        <el-table-column prop="time" label="时间" width="140" />
        <el-table-column prop="credit" label="学分" width="80" />
        <el-table-column label="容量" width="130">
          <template #default="{ row }">{{ row.selected }} / {{ row.capacity }}</template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button
                :disabled="row.selectedByMe || row.selected >= row.capacity"
                type="primary"
                size="small"
                @click="selectCourse(row)"
            >
              {{ row.selectedByMe ? '已选' : row.selected >= row.capacity ? '已满' : '选课' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { selectableCourses } from '../../data/mockData'

const semester = ref('2025-2026-1')
const keyword = ref('')
const courses = ref(selectableCourses.map((item) => ({ ...item })))

const filteredCourses = computed(() => {
  return courses.value.filter((item) => {
    return !keyword.value || item.name.includes(keyword.value) || item.teacher.includes(keyword.value)
  })
})

function selectCourse(row) {
  row.selectedByMe = true
  row.selected += 1
  ElMessage.success(`已选择 ${row.name}`)
}
</script>
