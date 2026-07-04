<template>
  <div class="page">
    <div>
      <h1 class="page-title">课程管理</h1>
      <div class="page-description">维护课程基础信息与本学期任课安排。</div>
    </div>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="课程库" name="courses">
        <el-card>
          <div class="toolbar">
            <div class="toolbar-left">
              <el-input v-model="keyword" placeholder="搜索课程编号或名称" clearable style="width: 260px" />
            </div>
            <el-button type="primary" @click="openCourseDialog()">新增课程</el-button>
          </div>
          <el-table v-loading="loading" :data="filteredCourses" border>
            <el-table-column prop="courseCode" label="课程编号" width="130" />
            <el-table-column prop="courseName" label="课程名称" />
            <el-table-column prop="credit" label="学分" width="90" />
            <el-table-column prop="totalHours" label="学时" width="90" />
            <el-table-column prop="courseType" label="课程类型" width="120" />
            <el-table-column prop="examType" label="考核方式" width="120" />
            <el-table-column label="操作" width="160">
              <template #default="{ row }">
                <el-button link type="primary" @click="openCourseDialog(row)">编辑</el-button>
                <el-button link type="danger" @click="removeCourseRow(row)">停用</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="任课安排" name="tasks">
        <el-card>
          <div class="toolbar">
            <div class="toolbar-left">
              <el-select v-model="semester" style="width: 160px">
                <el-option label="2025-2026-1" value="2025-2026-1" />
                <el-option label="2025-2026-2" value="2025-2026-2" />
              </el-select>
              <el-input v-model="taskKeyword" placeholder="搜索课程或教师" clearable style="width: 240px" />
            </div>
            <el-button type="primary" @click="openTaskDialog()">新增任课</el-button>
          </div>
          <el-table v-loading="loading" :data="filteredTasks" border>
            <el-table-column prop="courseName" label="课程" />
            <el-table-column prop="teacherName" label="教师" width="120" />
            <el-table-column prop="semester" label="学期" width="130" />
            <el-table-column label="时间" width="160">
              <template #default="{ row }">{{ formatTime(row) }}</template>
            </el-table-column>
            <el-table-column prop="classroom" label="教室" width="120" />
            <el-table-column label="容量" width="120">
              <template #default="{ row }">{{ row.selectedCount }} / {{ row.capacity }}</template>
            </el-table-column>
            <el-table-column label="操作" width="160">
              <template #default="{ row }">
                <el-button link type="primary" @click="openTaskDialog(row)">编辑</el-button>
                <el-button link type="danger" @click="removeTaskRow(row)">停用</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="courseDialog" :title="courseForm.id ? '编辑课程' : '新增课程'" width="520px">
      <el-form :model="courseForm" label-width="96px">
        <el-form-item label="课程编号"><el-input v-model="courseForm.courseCode" /></el-form-item>
        <el-form-item label="课程名称"><el-input v-model="courseForm.courseName" /></el-form-item>
        <el-form-item label="学分"><el-input-number v-model="courseForm.credit" :min="0.5" :step="0.5" style="width: 100%" /></el-form-item>
        <el-form-item label="学时"><el-input-number v-model="courseForm.totalHours" :min="1" style="width: 100%" /></el-form-item>
        <el-form-item label="课程类型">
          <el-select v-model="courseForm.courseType" style="width: 100%">
            <el-option label="必修" value="必修" />
            <el-option label="选修" value="选修" />
          </el-select>
        </el-form-item>
        <el-form-item label="考核方式">
          <el-select v-model="courseForm.examType" style="width: 100%">
            <el-option label="考试" value="考试" />
            <el-option label="考查" value="考查" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="courseDialog = false">取消</el-button>
        <el-button type="primary" @click="saveCourse">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="taskDialog" :title="taskForm.id ? '编辑任课' : '新增任课'" width="620px">
      <el-form :model="taskForm" label-width="96px">
        <el-form-item label="课程">
          <el-select v-model="taskForm.courseId" filterable style="width: 100%">
            <el-option v-for="item in courses" :key="item.id" :label="item.courseName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="教师">
          <el-select v-model="taskForm.teacherId" filterable style="width: 100%">
            <el-option v-for="item in teachers" :key="item.id" :label="item.teacherName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="学期"><el-input v-model="taskForm.semester" /></el-form-item>
        <el-form-item label="星期">
          <el-select v-model="taskForm.weekday" style="width: 100%">
            <el-option v-for="day in 7" :key="day" :label="`星期${dayText(day)}`" :value="day" />
          </el-select>
        </el-form-item>
        <el-form-item label="节次">
          <div class="inline-fields">
            <el-input-number v-model="taskForm.startSection" :min="1" :max="12" />
            <span class="muted">至</span>
            <el-input-number v-model="taskForm.endSection" :min="1" :max="12" />
          </div>
        </el-form-item>
        <el-form-item label="周次"><el-input v-model="taskForm.weeks" placeholder="如 1-16周" /></el-form-item>
        <el-form-item label="教室"><el-input v-model="taskForm.classroom" /></el-form-item>
        <el-form-item label="容量"><el-input-number v-model="taskForm.capacity" :min="1" style="width: 100%" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="taskDialog = false">取消</el-button>
        <el-button type="primary" @click="saveTask">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { createCourse, listCourses, removeCourse, updateCourse } from '../../api/course'
import { listTeachers } from '../../api/teacher'
import { createTeachingTask, listTeachingTasks, removeTeachingTask, updateTeachingTask } from '../../api/teachingTask'

const activeTab = ref('courses')
const keyword = ref('')
const taskKeyword = ref('')
const semester = ref('2025-2026-1')
const loading = ref(false)
const courseDialog = ref(false)
const taskDialog = ref(false)
const courses = ref([])
const teachers = ref([])
const tasks = ref([])
const courseForm = reactive({})
const taskForm = reactive({})

const filteredCourses = computed(() => courses.value.filter((item) => !keyword.value || item.courseCode.includes(keyword.value) || item.courseName.includes(keyword.value)))
const filteredTasks = computed(() => tasks.value.filter((item) => {
  const matchSemester = item.semester === semester.value
  const matchKeyword = !taskKeyword.value || item.courseName.includes(taskKeyword.value) || item.teacherName.includes(taskKeyword.value)
  return matchSemester && matchKeyword
}))

onMounted(refreshAll)

async function refreshAll() {
  loading.value = true
  try {
    ;[courses.value, teachers.value, tasks.value] = await Promise.all([listCourses(), listTeachers(), listTeachingTasks()])
  } finally {
    loading.value = false
  }
}

function reset(target, source) {
  Object.keys(target).forEach((key) => delete target[key])
  Object.assign(target, source)
}

function openCourseDialog(row = null) {
  reset(courseForm, row ? { ...row } : { credit: 3, totalHours: 48, courseType: '必修', examType: '考试' })
  courseDialog.value = true
}

function openTaskDialog(row = null) {
  reset(taskForm, row ? { ...row } : { semester: semester.value, weekday: 1, startSection: 1, endSection: 2, weeks: '1-16周', capacity: 60, taskStatus: 1 })
  taskDialog.value = true
}

async function saveCourse() {
  courseForm.id ? await updateCourse(courseForm.id, courseForm) : await createCourse(courseForm)
  courseDialog.value = false
  ElMessage.success('课程已保存')
  await refreshAll()
}

async function saveTask() {
  taskForm.id ? await updateTeachingTask(taskForm.id, taskForm) : await createTeachingTask(taskForm)
  taskDialog.value = false
  ElMessage.success('任课安排已保存')
  await refreshAll()
}

async function removeCourseRow(row) {
  await confirmRemove('确定停用该课程吗？')
  await removeCourse(row.id)
  await refreshAll()
}

async function removeTaskRow(row) {
  await confirmRemove('确定停用该任课安排吗？')
  await removeTeachingTask(row.id)
  await refreshAll()
}

function confirmRemove(message) {
  return ElMessageBox.confirm(message, '操作确认', { type: 'warning', confirmButtonText: '停用', cancelButtonText: '取消' })
}

function formatTime(row) {
  return `周${dayText(row.weekday)} ${row.startSection}-${row.endSection}节`
}

function dayText(day) {
  return ['一', '二', '三', '四', '五', '六', '日'][Number(day) - 1] || day
}
</script>

<style scoped>
.inline-fields {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>
