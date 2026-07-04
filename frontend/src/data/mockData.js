import { getCollegeStudentsReport, getOverviewReport } from '../api/report'
import { getStudentByUser } from '../api/student'
import { listStudentCourses } from '../api/studentCourse'
import { getTeacherByUser } from '../api/teacher'
import { listTeachingTasks } from '../api/teachingTask'

function numberText(value) {
  return Number(value || 0).toLocaleString('zh-CN')
}

function currentUser() {
  return JSON.parse(localStorage.getItem('currentUser') || 'null')
}

function formatWeekday(weekday) {
  return ['一', '二', '三', '四', '五', '六', '日'][Number(weekday) - 1] || weekday || '-'
}

export function formatCourseTime(row) {
  if (!row) return '-'
  return `周${formatWeekday(row.weekday)} ${row.startSection || '-'}-${row.endSection || '-'}节`
}

export async function getAdminDashboardData() {
  const [overview, collegeStudents] = await Promise.all([
    getOverviewReport(),
    getCollegeStudentsReport()
  ])

  const maxStudentCount = Math.max(...collegeStudents.map((item) => Number(item.value || 0)), 1)

  return {
    stats: [
      { title: '学生总数', value: numberText(overview.studentCount), extra: '来自 student 表在读记录' },
      { title: '教师总数', value: numberText(overview.teacherCount), extra: '来自 teacher 表启用记录' },
      { title: '课程总数', value: numberText(overview.courseCount), extra: '来自 course 表启用记录' },
      { title: '选课记录', value: numberText(overview.selectionCount), extra: `${numberText(overview.teachingTaskCount)} 个任课安排` }
    ],
    chartBars: collegeStudents.map((item) => ({
      name: item.name,
      value: Number(item.value || 0),
      height: `${Math.max(8, Math.round((Number(item.value || 0) / maxStudentCount) * 100))}%`
    }))
  }
}

export async function getTeacherDashboardData() {
  const user = currentUser()
  const teacher = await getTeacherByUser(user?.id)
  const [tasks, studentCourses] = await Promise.all([
    listTeachingTasks({ teacherId: teacher.id }),
    listStudentCourses({ teacherId: teacher.id })
  ])

  const selectedCount = tasks.reduce((sum, item) => sum + Number(item.selectedCount || 0), 0)
  const pendingCount = studentCourses.filter((item) => Number(item.gradeStatus) !== 1).length
  const gradedCount = studentCourses.length - pendingCount
  const completionRate = studentCourses.length ? Math.round((gradedCount / studentCourses.length) * 100) : 0

  return {
    stats: [
      { title: '本学期任课', value: numberText(tasks.length), extra: `${numberText(tasks.filter((item) => Number(item.taskStatus) === 1).length)} 门进行中` },
      { title: '选课学生', value: numberText(selectedCount), extra: '按任课安排汇总' },
      { title: '待录成绩', value: numberText(pendingCount), extra: '来自选课成绩状态' },
      { title: '平均完成率', value: `${completionRate}%`, extra: '成绩录入进度' }
    ],
    teacher,
    tasks: tasks.map((item) => ({
      ...item,
      course: item.courseName,
      time: formatCourseTime(item),
      room: item.classroom || '-',
      selected: Number(item.selectedCount || 0),
      pending: studentCourses.filter((row) => row.teachingTaskId === item.id && Number(row.gradeStatus) !== 1).length
    }))
  }
}

export async function getStudentDashboardData() {
  const user = currentUser()
  const student = await getStudentByUser(user?.id)
  const courses = await listStudentCourses({ studentId: student.id })
  const gradedRows = courses.filter((item) => Number(item.gradeStatus) === 1)
  const totalCredit = courses.reduce((sum, item) => sum + Number(item.credit || 0), 0)
  const averageScore = gradedRows.length
    ? (gradedRows.reduce((sum, item) => sum + Number(item.score || 0), 0) / gradedRows.length).toFixed(1)
    : '-'

  return {
    stats: [
      { title: '已选课程', value: numberText(courses.length), extra: `共 ${totalCredit.toFixed(1)} 学分` },
      { title: '课程安排', value: numberText(courses.length), extra: '来自当前选课记录' },
      { title: '已出成绩', value: numberText(gradedRows.length), extra: `平均分 ${averageScore}` },
      { title: '培养进度', value: `${Math.min(100, Math.round((totalCredit / 160) * 100))}%`, extra: '按 160 学分估算' }
    ],
    student,
    courses: courses.map((item) => ({
      ...item,
      name: item.courseName,
      time: formatCourseTime(item),
      room: item.classroom || '-'
    }))
  }
}
