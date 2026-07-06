<template>
  <div class="page">
    <div>
      <h1 class="page-title">可选课程</h1>
      <div class="page-description">查看本学期可选课程，通过表格或课程表完成选课和退课。</div>
    </div>

    <el-card>
      <div class="toolbar">
        <div class="toolbar-left">
          <el-select v-model="semester" style="width: 180px" @change="refresh">
            <el-option :label="currentSemester" :value="currentSemester" />
            <el-option label="2025-2026-1" value="2025-2026-1" />
            <el-option label="2025-2026-2" value="2025-2026-2" />
          </el-select>
          <el-select v-model="courseState" clearable placeholder="课程状态" style="width: 140px">
            <el-option label="全部" value="全部" />
            <el-option label="可选" value="可选" />
            <el-option label="已选" value="已选" />
            <el-option label="已满" value="已满" />
          </el-select>
          <el-input v-model="keyword" placeholder="搜索课程或教师" clearable style="width: 240px" />
        </div>
      </div>

      <el-tabs v-model="activeView">
        <el-tab-pane label="课程表" name="schedule">
          <div class="schedule-grid">
            <div class="schedule-cell schedule-head">节次</div>
            <div v-for="day in days" :key="day.value" class="schedule-cell schedule-head">{{ day.label }}</div>
            <template v-for="section in sections" :key="section">
              <div class="schedule-cell schedule-section">{{ section }}</div>
              <div v-for="day in days" :key="`${day.value}-${section}`" class="schedule-cell schedule-slot">
                <button
                  v-for="course in coursesAt(day.value, section)"
                  :key="course.id"
                  class="course-block"
                  :style="{ backgroundColor: courseColor(course.id) }"
                  @click="openDetail(course)"
                >
                  <strong>{{ course.courseName }}</strong>
                  <span>{{ course.teacherName }}</span>
                </button>
              </div>
            </template>
          </div>
        </el-tab-pane>

        <el-tab-pane label="课程列表" name="table">
          <el-table v-loading="loading" :data="filteredCourses" border empty-text="暂无可选课程">
            <el-table-column prop="courseCode" label="课程编号" width="120" />
            <el-table-column prop="courseName" label="课程名称" />
            <el-table-column prop="teacherName" label="教师" width="100" />
            <el-table-column label="时间" width="150">
              <template #default="{ row }">{{ formatTime(row) }}</template>
            </el-table-column>
            <el-table-column prop="credit" label="学分" width="80" />
            <el-table-column label="容量" width="120">
              <template #default="{ row }">{{ row.selectedCount }} / {{ row.capacity }}</template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="courseStatusType(row)" effect="plain">{{ courseStatusText(row) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="130">
              <template #default="{ row }">
                <el-button v-if="Number(row.selectedByMe) === 1" type="warning" size="small" @click="drop(row)">退课</el-button>
                <el-button v-else :disabled="row.selectedCount >= row.capacity" type="primary" size="small" @click="select(row)">
                  {{ row.selectedCount >= row.capacity ? '已满' : '选课' }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog v-model="detailVisible" title="课程详情" width="520px">
      <div v-if="detailCourse" class="detail-list">
        <div><span>课程</span><strong>{{ detailCourse.courseName }}</strong></div>
        <div><span>课程编号</span><strong>{{ detailCourse.courseCode }}</strong></div>
        <div><span>任课教师</span><strong>{{ detailCourse.teacherName }}</strong></div>
        <div><span>上课时间</span><strong>{{ formatTime(detailCourse) }}</strong></div>
        <div><span>周次</span><strong>{{ detailCourse.weeks || '-' }}</strong></div>
        <div><span>教室</span><strong>{{ detailCourse.classroom || '-' }}</strong></div>
        <div><span>容量</span><strong>{{ detailCourse.selectedCount }} / {{ detailCourse.capacity }}</strong></div>
        <div><span>状态</span><strong>{{ courseStatusText(detailCourse) }}</strong></div>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button v-if="detailCourse && Number(detailCourse.selectedByMe) === 1" type="warning" @click="drop(detailCourse)">退课</el-button>
        <el-button v-else-if="detailCourse" type="primary" :disabled="detailCourse.selectedCount >= detailCourse.capacity" @click="select(detailCourse)">选课</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getStudentByUser } from '../../api/student'
import { dropCourse, listSelectableCourses, selectCourse } from '../../api/studentCourse'
import { courseColor, getCurrentSemester } from '../../utils/academicMetrics'
import { filterRows } from '../../utils/filter'

const currentSemester = getCurrentSemester()
const semester = ref(currentSemester)
const keyword = ref('')
const courseState = ref('全部')
const activeView = ref('schedule')
const loading = ref(false)
const student = ref(null)
const courses = ref([])
const detailVisible = ref(false)
const detailCourse = ref(null)
const currentUser = JSON.parse(localStorage.getItem('currentUser') || 'null')
const days = [
  { label: '周一', value: 1 },
  { label: '周二', value: 2 },
  { label: '周三', value: 3 },
  { label: '周四', value: 4 },
  { label: '周五', value: 5 },
  { label: '周六', value: 6 },
  { label: '周日', value: 7 }
]
const sections = Array.from({ length: 12 }, (_, index) => index + 1)

const filteredCourses = computed(() =>
  filterRows(courses.value, {
    keyword: keyword.value,
    fields: ['courseCode', 'courseName', 'teacherName'],
    predicates: [
      (row) => {
        if (courseState.value === '全部') return true
        if (courseState.value === '已选') return Number(row.selectedByMe) === 1
        if (courseState.value === '已满') return Number(row.selectedCount) >= Number(row.capacity)
        return Number(row.selectedByMe) !== 1 && Number(row.selectedCount) < Number(row.capacity)
      }
    ]
  })
)

onMounted(refresh)

async function refresh() {
  loading.value = true
  try {
    if (!student.value) {
      student.value = await getStudentByUser(currentUser.id)
    }
    courses.value = await listSelectableCourses({ studentId: student.value.id, semester: semester.value })
  } finally {
    loading.value = false
  }
}

function coursesAt(day, section) {
  return filteredCourses.value.filter((course) => Number(course.weekday) === day && Number(course.startSection) <= section && Number(course.endSection) >= section)
}

function openDetail(course) {
  detailCourse.value = course
  detailVisible.value = true
}

async function select(row) {
  await selectCourse({ studentId: student.value.id, teachingTaskId: row.id })
  ElMessage.success('选课成功')
  detailVisible.value = false
  await refresh()
}

async function drop(row) {
  await dropCourse(row.studentCourseId)
  ElMessage.success('退课成功')
  detailVisible.value = false
  await refresh()
}

function formatTime(row) {
  return `周${['一', '二', '三', '四', '五', '六', '日'][Number(row.weekday) - 1] || row.weekday} ${row.startSection}-${row.endSection}节`
}

function courseStatusText(row) {
  if (Number(row.selectedByMe) === 1) return '已选'
  if (Number(row.selectedCount) >= Number(row.capacity)) return '已满'
  return '可选'
}

function courseStatusType(row) {
  if (Number(row.selectedByMe) === 1) return 'success'
  if (Number(row.selectedCount) >= Number(row.capacity)) return 'danger'
  return 'info'
}
</script>

<style scoped>
.schedule-grid {
  display: grid;
  grid-template-columns: 64px repeat(7, minmax(96px, 1fr));
  overflow-x: auto;
  border-top: 1px solid var(--border);
  border-left: 1px solid var(--border);
}

.schedule-cell {
  min-height: 56px;
  padding: 6px;
  border-right: 1px solid var(--border);
  border-bottom: 1px solid var(--border);
}

.schedule-head,
.schedule-section {
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-muted);
  font-size: 13px;
  background: #f9fafb;
}

.schedule-slot {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.course-block {
  width: 100%;
  min-height: 42px;
  padding: 6px;
  border: 0;
  border-radius: 6px;
  color: #fff;
  cursor: pointer;
  text-align: left;
}

.course-block span,
.course-block strong {
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.detail-list {
  display: grid;
  gap: 12px;
}

.detail-list div {
  display: flex;
  justify-content: space-between;
  gap: 16px;
}
</style>
