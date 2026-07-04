export const overviewStats = {
  admin: [
    { title: '学生总数', value: '1,280', extra: '覆盖 32 个教学班' },
    { title: '教师总数', value: '86', extra: '12 个教研室' },
    { title: '课程总数', value: '142', extra: '本学期开放 58 门' },
    { title: '选课记录', value: '3,560', extra: '平均满课率 74%' }
  ],
  teacher: [
    { title: '本学期任课', value: '4', extra: '含 2 门核心课程' },
    { title: '选课学生', value: '186', extra: '较上周增加 12 人' },
    { title: '待录成绩', value: '48', extra: '数据库原理未完成' },
    { title: '平均完成率', value: '82%', extra: '成绩录入进度' }
  ],
  student: [
    { title: '已选课程', value: '6', extra: '共 17.5 学分' },
    { title: '本周课程', value: '18', extra: '包含 3 次实验课' },
    { title: '已出成绩', value: '4', extra: '平均分 87.3' },
    { title: '培养进度', value: '68%', extra: '专业要求学分' }
  ]
}

export const colleges = [
  { code: 'CS', name: '计算机学院', majors: 4, classes: 12, students: 486, status: '正常' },
  { code: 'AI', name: '人工智能学院', majors: 3, classes: 8, students: 312, status: '正常' },
  { code: 'SE', name: '软件工程学院', majors: 2, classes: 6, students: 248, status: '正常' }
]

export const students = [
  { no: '20240101', name: '李明', className: '软工 2401', major: '软件工程', status: '在读' },
  { no: '20240102', name: '陈雨', className: '计科 2402', major: '计算机科学与技术', status: '在读' },
  { no: '20230108', name: '王晨', className: '智能 2301', major: '人工智能', status: '在读' }
]

export const teachers = [
  { no: 'T202001', name: '张老师', department: '软件工程教研室', title: '副教授', courses: 3 },
  { no: 'T202014', name: '刘老师', department: '数据库教研室', title: '讲师', courses: 2 },
  { no: 'T202022', name: '周老师', department: '人工智能教研室', title: '教授', courses: 4 }
]

export const courses = [
  { code: 'CS101', name: '程序设计基础', credit: 3.5, teacher: '张老师', selected: 58, capacity: 60 },
  { code: 'CS208', name: '数据库原理', credit: 3, teacher: '刘老师', selected: 46, capacity: 55 },
  { code: 'AI301', name: '机器学习导论', credit: 3, teacher: '周老师', selected: 39, capacity: 45 }
]

export const teacherTasks = [
  { course: '数据库原理', semester: '2025-2026-1', time: '周二 3-4 节', room: 'A302', selected: 46, pending: 12 },
  { course: 'Java Web 开发', semester: '2025-2026-1', time: '周四 5-6 节', room: 'B206', selected: 52, pending: 36 },
  { course: '数据库课程设计', semester: '2025-2026-1', time: '周五 1-2 节', room: '实验楼 403', selected: 34, pending: 0 }
]

export const gradeRows = [
  { studentNo: '20240101', studentName: '李明', course: '数据库原理', score: 88, status: '已录入' },
  { studentNo: '20240102', studentName: '陈雨', course: '数据库原理', score: 91, status: '已录入' },
  { studentNo: '20240103', studentName: '赵远', course: '数据库原理', score: null, status: '未录入' }
]

export const selectableCourses = [
  { code: 'CS208', name: '数据库原理', teacher: '刘老师', time: '周二 3-4 节', credit: 3, selected: 46, capacity: 55, selectedByMe: true },
  { code: 'WEB301', name: 'Java Web 开发', teacher: '张老师', time: '周四 5-6 节', credit: 3, selected: 52, capacity: 60, selectedByMe: false },
  { code: 'AI301', name: '机器学习导论', teacher: '周老师', time: '周三 7-8 节', credit: 3, selected: 45, capacity: 45, selectedByMe: false }
]

export const studentGrades = [
  { course: '程序设计基础', credit: 3.5, teacher: '张老师', score: 92, grade: '优秀' },
  { course: '高等数学', credit: 5, teacher: '黄老师', score: 86, grade: '良好' },
  { course: '大学英语', credit: 2, teacher: '何老师', score: 89, grade: '良好' },
  { course: '数据库原理', credit: 3, teacher: '刘老师', score: null, grade: '未录入' }
]
