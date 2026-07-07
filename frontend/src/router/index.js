import { createRouter, createWebHistory } from 'vue-router'

import Login from '../views/Login.vue'
import AdminLayout from '../layout/AdminLayout.vue'
import AdminDashboard from '../views/admin/AdminDashboard.vue'
import AcademicManagement from '../views/admin/AcademicManagement.vue'
import PeopleManagement from '../views/admin/PeopleManagement.vue'
import CourseManagement from '../views/admin/CourseManagement.vue'
import AdminReports from '../views/admin/AdminReports.vue'
import OperationLogs from '../views/admin/OperationLogs.vue'
import TeacherDashboard from '../views/teacher/TeacherDashboard.vue'
import TeacherCourses from '../views/teacher/TeacherCourses.vue'
import GradeManagement from '../views/teacher/GradeManagement.vue'
import TeacherReports from '../views/teacher/TeacherReports.vue'
import StudentDashboard from '../views/student/StudentDashboard.vue'
import CourseSelect from '../views/student/CourseSelect.vue'
import MyGrades from '../views/student/MyGrades.vue'
import Unauthorized from '../views/error/Unauthorized.vue'
import NotFound from '../views/error/NotFound.vue'

function readCurrentUser() {
    try {
        const user = JSON.parse(localStorage.getItem('currentUser') || 'null')
        if (!user?.role) {
            localStorage.removeItem('currentUser')
            return null
        }
        return user
    } catch (error) {
        localStorage.removeItem('currentUser')
        return null
    }
}

const routes = [
    {
        path: '/',
        redirect: () => {
            const user = readCurrentUser()
            if (!user) {
                return '/login'
            }
            return `/${user.role.toLowerCase()}/dashboard`
        }
    },
    {
        path: '/login',
        component: Login
    },
    {
        path: '/401',
        component: Unauthorized
    },
    {
        path: '/404',
        component: NotFound
    },
    {
        path: '/admin',
        component: AdminLayout,
        meta: { role: 'ADMIN' },
        redirect: '/admin/dashboard',
        children: [
            {
                path: 'dashboard',
                component: AdminDashboard,
                meta: { title: '数据看板', role: 'ADMIN' }
            },
            {
                path: 'academic',
                component: AcademicManagement,
                meta: { title: '组织管理', role: 'ADMIN' }
            },
            {
                path: 'people',
                component: PeopleManagement,
                meta: { title: '人员管理', role: 'ADMIN' }
            },
            {
                path: 'courses',
                component: CourseManagement,
                meta: { title: '课程管理', role: 'ADMIN' }
            },
            {
                path: 'reports',
                component: AdminReports,
                meta: { title: '统计报表', role: 'ADMIN' }
            },
            {
                path: 'logs',
                component: OperationLogs,
                meta: { title: '操作日志', role: 'ADMIN' }
            }
        ]
    },
    {
        path: '/teacher',
        component: AdminLayout,
        meta: { role: 'TEACHER' },
        redirect: '/teacher/dashboard',
        children: [
            {
                path: 'dashboard',
                component: TeacherDashboard,
                meta: { title: '教师工作台', role: 'TEACHER' }
            },
            {
                path: 'courses',
                component: TeacherCourses,
                meta: { title: '我的任课', role: 'TEACHER' }
            },
            {
                path: 'grades',
                component: GradeManagement,
                meta: { title: '成绩录入', role: 'TEACHER' }
            },
            {
                path: 'reports',
                component: TeacherReports,
                meta: { title: '成绩分析', role: 'TEACHER' }
            }
        ]
    },
    {
        path: '/student',
        component: AdminLayout,
        meta: { role: 'STUDENT' },
        redirect: '/student/dashboard',
        children: [
            {
                path: 'dashboard',
                component: StudentDashboard,
                meta: { title: '学生工作台', role: 'STUDENT' }
            },
            {
                path: 'course-select',
                component: CourseSelect,
                meta: { title: '可选课程', role: 'STUDENT' }
            },
            {
                path: 'my-grades',
                component: MyGrades,
                meta: { title: '我的成绩', role: 'STUDENT' }
            }
        ]
    },
    {
        path: '/:pathMatch(.*)*',
        redirect: '/404'
    }
]

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes
})

router.beforeEach((to) => {
    if (to.path === '/login' || to.path === '/401' || to.path === '/404') {
        return true
    }

    const currentUser = readCurrentUser()
    if (!currentUser) {
        return '/login'
    }

    const requiredRole = to.meta.role || to.matched.find((record) => record.meta.role)?.meta.role
    if (requiredRole && currentUser.role !== requiredRole) {
        return '/401'
    }

    return true
})

export default router
