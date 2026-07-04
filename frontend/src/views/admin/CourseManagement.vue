<template>
  <div class="page">
    <div>
      <h1 class="page-title">课程管理</h1>
      <div class="page-description">维护课程基础信息，任课安排后续在 teaching_task 模块接入。</div>
    </div>

    <el-card>
      <div class="toolbar">
        <div class="toolbar-left">
          <el-input v-model="keyword" placeholder="搜索课程编号或名称" clearable style="width: 260px" />
          <el-select v-model="status" style="width: 128px">
            <el-option label="全部" value="全部" />
            <el-option label="正常" value="1" />
            <el-option label="停用" value="0" />
          </el-select>
        </div>
        <el-button type="primary" @click="openCreateDialog">新增课程</el-button>
      </div>

      <el-table v-loading="loading" :data="filteredCourses" border>
        <el-table-column prop="courseCode" label="课程编号" width="130" />
        <el-table-column prop="courseName" label="课程名称" />
        <el-table-column prop="credit" label="学分" width="90" />
        <el-table-column prop="totalHours" label="学时" width="90" />
        <el-table-column prop="courseType" label="课程类型" width="120" />
        <el-table-column prop="examType" label="考核方式" width="120" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'info' : 'success'" effect="plain">
              {{ row.status === 0 ? '停用' : '正常' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEditDialog(row)">编辑</el-button>
            <el-button link type="danger" @click="handleRemove(row)">停用</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
        v-model="dialogVisible"
        :title="dialogMode === 'create' ? '新增课程' : '编辑课程'"
        width="520px"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="96px">
        <el-form-item label="课程编号" prop="courseCode">
          <el-input v-model="form.courseCode" placeholder="请输入课程编号" />
        </el-form-item>
        <el-form-item label="课程名称" prop="courseName">
          <el-input v-model="form.courseName" placeholder="请输入课程名称" />
        </el-form-item>
        <el-form-item label="学分" prop="credit">
          <el-input-number v-model="form.credit" :min="0.5" :step="0.5" style="width: 100%" />
        </el-form-item>
        <el-form-item label="学时" prop="totalHours">
          <el-input-number v-model="form.totalHours" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="课程类型">
          <el-select v-model="form.courseType" placeholder="请选择课程类型" style="width: 100%">
            <el-option label="必修" value="必修" />
            <el-option label="选修" value="选修" />
          </el-select>
        </el-form-item>
        <el-form-item label="考核方式">
          <el-select v-model="form.examType" placeholder="请选择考核方式" style="width: 100%">
            <el-option label="考试" value="考试" />
            <el-option label="考查" value="考查" />
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { createCourse, listCourses, removeCourse, updateCourse } from '../../api/course'

const keyword = ref('')
const status = ref('全部')
const loading = ref(false)
const dialogVisible = ref(false)
const dialogMode = ref('create')
const formRef = ref()
const courses = ref([])
const form = reactive({})

const defaultForm = {
  courseCode: '',
  courseName: '',
  credit: 3,
  totalHours: 48,
  courseType: '必修',
  examType: '考试'
}

const rules = {
  courseCode: [{ required: true, message: '请输入课程编号', trigger: 'blur' }],
  courseName: [{ required: true, message: '请输入课程名称', trigger: 'blur' }],
  credit: [{ required: true, message: '请输入学分', trigger: 'change' }]
}

const filteredCourses = computed(() => {
  return courses.value.filter((item) => {
    const matchKeyword = !keyword.value || item.courseCode.includes(keyword.value) || item.courseName.includes(keyword.value)
    const matchStatus = status.value === '全部' || String(item.status ?? 1) === status.value
    return matchKeyword && matchStatus
  })
})

onMounted(() => {
  refreshCourses()
})

async function refreshCourses() {
  loading.value = true
  try {
    courses.value = await listCourses()
  } finally {
    loading.value = false
  }
}

function resetForm(data = defaultForm) {
  Object.keys(form).forEach((key) => delete form[key])
  Object.assign(form, JSON.parse(JSON.stringify(data)))
}

function openCreateDialog() {
  dialogMode.value = 'create'
  resetForm()
  dialogVisible.value = true
}

function openEditDialog(row) {
  dialogMode.value = 'edit'
  resetForm(row)
  dialogVisible.value = true
}

async function submitForm() {
  await formRef.value.validate()
  if (dialogMode.value === 'create') {
    await createCourse(form)
  } else {
    await updateCourse(form.id, form)
  }
  ElMessage.success('保存成功')
  dialogVisible.value = false
  await refreshCourses()
}

async function handleRemove(row) {
  await ElMessageBox.confirm('确定停用该课程吗？', '操作确认', {
    type: 'warning',
    confirmButtonText: '停用',
    cancelButtonText: '取消'
  })
  await removeCourse(row.id)
  ElMessage.success('停用成功')
  await refreshCourses()
}
</script>
