<template>
  <div class="page">
    <div>
      <h1 class="page-title">人员管理</h1>
      <div class="page-description">维护登录账号、学生档案、教师档案和导师指导关系。</div>
    </div>

    <el-tabs v-model="activeTab" class="management-tabs">
      <el-tab-pane label="学生" name="students">
        <el-card>
          <div class="toolbar">
            <el-input v-model="studentKeyword" placeholder="搜索学号、姓名、班级" clearable style="width: 280px" />
            <el-button type="primary" @click="openStudentDialog()">新增学生</el-button>
          </div>
          <el-table v-loading="loading" :data="filteredStudents" border>
            <el-table-column prop="studentNo" label="学号" width="130" />
            <el-table-column prop="studentName" label="姓名" width="110" />
            <el-table-column prop="collegeName" label="学院" />
            <el-table-column prop="majorName" label="专业" />
            <el-table-column prop="className" label="班级" width="130" />
            <el-table-column prop="username" label="登录账号" width="130" />
            <el-table-column label="操作" width="150">
              <template #default="{ row }">
                <el-button link type="primary" @click="openStudentDialog(row)">编辑</el-button>
                <el-button link type="danger" @click="removeStudentRow(row)">停用</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="教师" name="teachers">
        <el-card>
          <div class="toolbar">
            <el-input v-model="teacherKeyword" placeholder="搜索工号、姓名、教研室" clearable style="width: 280px" />
            <el-button type="primary" @click="openTeacherDialog()">新增教师</el-button>
          </div>
          <el-table v-loading="loading" :data="filteredTeachers" border>
            <el-table-column prop="teacherNo" label="工号" width="130" />
            <el-table-column prop="teacherName" label="姓名" width="110" />
            <el-table-column prop="collegeName" label="学院" />
            <el-table-column prop="departmentName" label="教研室" />
            <el-table-column prop="title" label="职称" width="120" />
            <el-table-column prop="username" label="登录账号" width="130" />
            <el-table-column label="操作" width="150">
              <template #default="{ row }">
                <el-button link type="primary" @click="openTeacherDialog(row)">编辑</el-button>
                <el-button link type="danger" @click="removeTeacherRow(row)">停用</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="账号" name="users">
        <el-card>
          <div class="toolbar">
            <el-input v-model="userKeyword" placeholder="搜索用户名或角色" clearable style="width: 260px" />
            <el-button type="primary" @click="openUserDialog()">新增账号</el-button>
          </div>
          <el-table v-loading="loading" :data="filteredUsers" border>
            <el-table-column prop="username" label="用户名" />
            <el-table-column label="角色" width="130">
              <template #default="{ row }">
                <el-tag effect="plain">{{ roleText(row.role) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="lastLoginAt" label="最后登录" width="190" />
            <el-table-column label="操作" width="150">
              <template #default="{ row }">
                <el-button link type="primary" @click="openUserDialog(row)">编辑</el-button>
                <el-button link type="danger" @click="removeUserRow(row)">停用</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="指导关系" name="guides">
        <el-card>
          <div class="toolbar">
            <span class="section-title">教师指导学生</span>
            <el-button type="primary" @click="openGuideDialog()">新增关系</el-button>
          </div>
          <el-table v-loading="loading" :data="guides" border>
            <el-table-column prop="teacherName" label="教师" />
            <el-table-column prop="studentNo" label="学号" width="130" />
            <el-table-column prop="studentName" label="学生" />
            <el-table-column prop="guideType" label="指导类型" />
            <el-table-column prop="startDate" label="开始日期" width="130" />
            <el-table-column label="操作" width="150">
              <template #default="{ row }">
                <el-button link type="primary" @click="openGuideDialog(row)">编辑</el-button>
                <el-button link type="danger" @click="removeGuideRow(row)">停用</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="userDialog" :title="editingUser.id ? '编辑账号' : '新增账号'" width="480px">
      <el-form :model="editingUser" label-width="88px">
        <el-form-item label="用户名"><el-input v-model="editingUser.username" /></el-form-item>
        <el-form-item label="密码"><el-input v-model="editingUser.password" type="password" show-password placeholder="编辑时留空表示不修改" /></el-form-item>
        <el-form-item label="角色">
          <el-select v-model="editingUser.role" style="width: 100%">
            <el-option label="管理员" value="ADMIN" />
            <el-option label="教师" value="TEACHER" />
            <el-option label="学生" value="STUDENT" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="userDialog = false">取消</el-button>
        <el-button type="primary" @click="saveUser">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="studentDialog" :title="editingStudent.id ? '编辑学生' : '新增学生'" width="640px">
      <el-form :model="editingStudent" label-width="88px">
        <el-form-item label="绑定账号">
          <el-select v-model="editingStudent.userId" filterable style="width: 100%">
            <el-option v-for="user in studentUsers" :key="user.id" :label="user.username" :value="user.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属班级">
          <el-select v-model="editingStudent.classId" filterable style="width: 100%">
            <el-option v-for="item in classes" :key="item.id" :label="item.className" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="学号"><el-input v-model="editingStudent.studentNo" /></el-form-item>
        <el-form-item label="姓名"><el-input v-model="editingStudent.studentName" /></el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="editingStudent.gender">
            <el-radio-button label="M">男</el-radio-button>
            <el-radio-button label="F">女</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="入学年份"><el-input-number v-model="editingStudent.enrollmentYear" :min="2000" :max="2100" /></el-form-item>
        <el-form-item label="联系方式"><el-input v-model="editingStudent.phone" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="editingStudent.email" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="studentDialog = false">取消</el-button>
        <el-button type="primary" @click="saveStudent">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="teacherDialog" :title="editingTeacher.id ? '编辑教师' : '新增教师'" width="640px">
      <el-form :model="editingTeacher" label-width="88px">
        <el-form-item label="绑定账号">
          <el-select v-model="editingTeacher.userId" filterable style="width: 100%">
            <el-option v-for="user in teacherUsers" :key="user.id" :label="user.username" :value="user.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="教研室">
          <el-select v-model="editingTeacher.departmentId" filterable style="width: 100%">
            <el-option v-for="item in departments" :key="item.id" :label="item.departmentName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="工号"><el-input v-model="editingTeacher.teacherNo" /></el-form-item>
        <el-form-item label="姓名"><el-input v-model="editingTeacher.teacherName" /></el-form-item>
        <el-form-item label="职称"><el-input v-model="editingTeacher.title" /></el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="editingTeacher.gender">
            <el-radio-button label="M">男</el-radio-button>
            <el-radio-button label="F">女</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="联系方式"><el-input v-model="editingTeacher.phone" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="editingTeacher.email" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="teacherDialog = false">取消</el-button>
        <el-button type="primary" @click="saveTeacher">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="guideDialog" :title="editingGuide.id ? '编辑指导关系' : '新增指导关系'" width="560px">
      <el-form :model="editingGuide" label-width="96px">
        <el-form-item label="教师">
          <el-select v-model="editingGuide.teacherId" filterable style="width: 100%">
            <el-option v-for="item in teachers" :key="item.id" :label="item.teacherName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="学生">
          <el-select v-model="editingGuide.studentId" filterable style="width: 100%">
            <el-option v-for="item in students" :key="item.id" :label="`${item.studentNo} ${item.studentName}`" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="指导类型"><el-input v-model="editingGuide.guideType" /></el-form-item>
        <el-form-item label="开始日期"><el-date-picker v-model="editingGuide.startDate" value-format="YYYY-MM-DD" style="width: 100%" /></el-form-item>
        <el-form-item label="结束日期"><el-date-picker v-model="editingGuide.endDate" value-format="YYYY-MM-DD" style="width: 100%" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="guideDialog = false">取消</el-button>
        <el-button type="primary" @click="saveGuide">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listClassInfos } from '../../api/classInfo'
import { listDepartments } from '../../api/department'
import { createStudent, listStudents, removeStudent, updateStudent } from '../../api/student'
import { createTeacher, listTeachers, removeTeacher, updateTeacher } from '../../api/teacher'
import { createUser, listUsers, removeUser, updateUser } from '../../api/user'
import { createTeacherStudent, listTeacherStudents, removeTeacherStudent, updateTeacherStudent } from '../../api/teacherStudent'

const activeTab = ref('students')
const loading = ref(false)
const students = ref([])
const teachers = ref([])
const users = ref([])
const guides = ref([])
const classes = ref([])
const departments = ref([])
const studentKeyword = ref('')
const teacherKeyword = ref('')
const userKeyword = ref('')
const userDialog = ref(false)
const studentDialog = ref(false)
const teacherDialog = ref(false)
const guideDialog = ref(false)
const editingUser = reactive({})
const editingStudent = reactive({})
const editingTeacher = reactive({})
const editingGuide = reactive({})

const studentUsers = computed(() => users.value.filter((item) => item.role === 'STUDENT'))
const teacherUsers = computed(() => users.value.filter((item) => item.role === 'TEACHER'))
const filteredStudents = computed(() => filterRows(students.value, studentKeyword.value, ['studentNo', 'studentName', 'className', 'majorName']))
const filteredTeachers = computed(() => filterRows(teachers.value, teacherKeyword.value, ['teacherNo', 'teacherName', 'departmentName']))
const filteredUsers = computed(() => filterRows(users.value, userKeyword.value, ['username', 'role']))

onMounted(refreshAll)

async function refreshAll() {
  loading.value = true
  try {
    ;[students.value, teachers.value, users.value, guides.value, classes.value, departments.value] = await Promise.all([
      listStudents(),
      listTeachers(),
      listUsers(),
      listTeacherStudents(),
      listClassInfos(),
      listDepartments()
    ])
  } finally {
    loading.value = false
  }
}

function filterRows(rows, keyword, fields) {
  if (!keyword) return rows
  return rows.filter((row) => fields.some((field) => String(row[field] || '').includes(keyword)))
}

function reset(target, source) {
  Object.keys(target).forEach((key) => delete target[key])
  Object.assign(target, source)
}

function openUserDialog(row = null) {
  reset(editingUser, row ? { ...row, password: '' } : { username: '', password: '123456', role: 'STUDENT' })
  userDialog.value = true
}

function openStudentDialog(row = null) {
  reset(editingStudent, row ? { ...row } : { gender: 'M', enrollmentYear: new Date().getFullYear() })
  studentDialog.value = true
}

function openTeacherDialog(row = null) {
  reset(editingTeacher, row ? { ...row } : { gender: 'M', title: '讲师' })
  teacherDialog.value = true
}

function openGuideDialog(row = null) {
  reset(editingGuide, row ? { ...row } : { guideType: '课程设计指导' })
  guideDialog.value = true
}

async function saveUser() {
  editingUser.id ? await updateUser(editingUser.id, editingUser) : await createUser(editingUser)
  userDialog.value = false
  ElMessage.success('账号已保存')
  await refreshAll()
}

async function saveStudent() {
  editingStudent.id ? await updateStudent(editingStudent.id, editingStudent) : await createStudent(editingStudent)
  studentDialog.value = false
  ElMessage.success('学生已保存')
  await refreshAll()
}

async function saveTeacher() {
  editingTeacher.id ? await updateTeacher(editingTeacher.id, editingTeacher) : await createTeacher(editingTeacher)
  teacherDialog.value = false
  ElMessage.success('教师已保存')
  await refreshAll()
}

async function saveGuide() {
  editingGuide.id ? await updateTeacherStudent(editingGuide.id, editingGuide) : await createTeacherStudent(editingGuide)
  guideDialog.value = false
  ElMessage.success('指导关系已保存')
  await refreshAll()
}

async function removeUserRow(row) {
  await confirmRemove('确定停用该账号吗？')
  await removeUser(row.id)
  await refreshAll()
}

async function removeStudentRow(row) {
  await confirmRemove('确定停用该学生档案吗？')
  await removeStudent(row.id)
  await refreshAll()
}

async function removeTeacherRow(row) {
  await confirmRemove('确定停用该教师档案吗？')
  await removeTeacher(row.id)
  await refreshAll()
}

async function removeGuideRow(row) {
  await confirmRemove('确定停用该指导关系吗？')
  await removeTeacherStudent(row.id)
  await refreshAll()
}

function confirmRemove(message) {
  return ElMessageBox.confirm(message, '操作确认', { type: 'warning', confirmButtonText: '停用', cancelButtonText: '取消' })
}

function roleText(role) {
  return { ADMIN: '管理员', TEACHER: '教师', STUDENT: '学生' }[role] || role
}
</script>
