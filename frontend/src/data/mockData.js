import { getCollegeStudentsReport, getOverviewReport } from '../api/report'
import { getStudentByUser } from '../api/student'
import { listStudentCourses } from '../api/studentCourse'
import { getTeacherByUser, listTeachers } from '../api/teacher'
import { listTeachingTasks } from '../api/teachingTask'

function numberText(value) {
  return Number(value || 0).toLocaleString('zh-CN')
}

function currentUser() {
  return JSON.parse(localStorage.getItem('currentUser') || 'null')
}

function toNumber(value) {
  const parsed = Number(value)
  return Number.isFinite(parsed) ? parsed : 0
}

function formatWeekday(weekday) {
  return ['一', '二', '三', '四', '五', '六', '日'][Number(weekday) - 1] || weekday || '-'
}

function createCollegeRecord(collegeName) {
  return {
    collegeName,
    studentCount: 0,
    teacherCount: 0,
    studentShare: 0,
    teacherShare: 0,
    totalCount: 0
  }
}

function buildCollegeSummary(studentRows, teacherRows) {
  const summaryMap = new Map()

  studentRows.forEach((row) => {
    const collegeName = row.name || '未分类'
    if (!summaryMap.has(collegeName)) {
      summaryMap.set(collegeName, createCollegeRecord(collegeName))
    }
    summaryMap.get(collegeName).studentCount = toNumber(row.value)
  })

  teacherRows.forEach((row) => {
    const collegeName = row.collegeName || '未分类'
    if (!summaryMap.has(collegeName)) {
      summaryMap.set(collegeName, createCollegeRecord(collegeName))
    }
    summaryMap.get(collegeName).teacherCount += 1
  })

  const totalStudents = studentRows.reduce((sum, row) => sum + toNumber(row.value), 0)
  const totalTeachers = teacherRows.length

  return Array.from(summaryMap.values())
    .map((row) => ({
      ...row,
      totalCount: row.studentCount + row.teacherCount,
      studentShare: totalStudents ? row.studentCount / totalStudents : 0,
      teacherShare: totalTeachers ? row.teacherCount / totalTeachers : 0
    }))
    .sort((left, right) => right.totalCount - left.totalCount || left.collegeName.localeCompare(right.collegeName, 'zh-Hans-CN'))
}

function buildPieRows(rows, field) {
  return rows
    .filter((row) => toNumber(row[field]) > 0)
    .map((row) => ({
      name: row.collegeName,
      value: toNumber(row[field])
    }))
}

export function formatCourseTime(row) {
  if (!row) {
    return '-'
  }

  return `周${formatWeekday(row.weekday)} ${row.startSection || '-'}-${row.endSection || '-'}节`
}

export async function getAdminDashboardData() {
  const [overview, collegeStudents, teachers] = await Promise.all([
    getOverviewReport(),
    getCollegeStudentsReport(),
    listTeachers()
  ])

  const collegeSummary = buildCollegeSummary(collegeStudents, teachers)

  return {
    stats: [
      { title: '学生总数', value: numberText(overview.studentCount), extra: '来自 student 表正常学籍记录' },
      { title: '教师总数', value: numberText(overview.teacherCount), extra: '来自 teacher 表正常在职记录' },
      { title: '课程总数', value: numberText(overview.courseCount), extra: '来自 course 表启用记录' },
      { title: '选课记录', value: numberText(overview.selectionCount), extra: `${numberText(overview.teachingTaskCount)} 个任课安排` }
    ],
    collegeSummary,
    studentPieRows: buildPieRows(collegeSummary, 'studentCount'),
    teacherPieRows: buildPieRows(collegeSummary, 'teacherCount')
  }
}

export async function getTeacherDashboardData() {
  const user = currentUser()
  const teacher = await getTeacherByUser(user?.id)
  const [tasks, studentCourses] = await Promise.all([
    listTeachingTasks({ teacherId: teacher.id }),
    listStudentCourses({ teacherId: teacher.id })
  ])

  const selectedCount = tasks.reduce((sum, item) => sum + toNumber(item.selectedCount), 0)
  const activeTaskCount = tasks.filter((item) => Number(item.taskStatus) === 1).length
  const pendingCount = studentCourses.filter((item) => Number(item.gradeStatus) !== 1).length
  const gradedCount = studentCourses.length - pendingCount
  const completionRate = studentCourses.length ? Math.round((gradedCount / studentCourses.length) * 100) : 0

  return {
    stats: [
      { title: '本学期任课', value: numberText(tasks.length), extra: `${numberText(activeTaskCount)} 门进行中` },
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
      selected: toNumber(item.selectedCount),
      pending: studentCourses.filter((row) => row.teachingTaskId === item.id && Number(row.gradeStatus) !== 1).length
    }))
  }
}

export async function getStudentDashboardData() {
  const user = currentUser()
  const student = await getStudentByUser(user?.id)
  const courses = await listStudentCourses({ studentId: student.id })
  const gradedRows = courses.filter((item) => Number(item.gradeStatus) === 1)
  const totalCredit = courses.reduce((sum, item) => sum + toNumber(item.credit), 0)
  const averageScore = gradedRows.length
    ? (gradedRows.reduce((sum, item) => sum + toNumber(item.score), 0) / gradedRows.length).toFixed(1)
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
