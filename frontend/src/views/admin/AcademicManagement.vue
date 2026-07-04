<template>
  <div class="page">
    <div>
      <h1 class="page-title">组织管理</h1>
      <div class="page-description">维护学院、专业、教研室和班级等基础组织数据。</div>
    </div>

    <el-card>
      <div class="toolbar">
        <div class="toolbar-left">
          <el-input v-model="keyword" :placeholder="activeConfig.searchPlaceholder" clearable style="width: 260px" />
          <el-select v-model="status" placeholder="状态" style="width: 128px">
            <el-option label="全部" value="全部" />
            <el-option label="正常" value="1" />
            <el-option label="停用" value="0" />
          </el-select>
        </div>
        <el-button type="primary" @click="openCreateDialog">新增{{ activeConfig.title }}</el-button>
      </div>

      <el-tabs v-model="activeTab">
        <el-tab-pane label="学院" name="college" />
        <el-tab-pane label="专业" name="major" />
        <el-tab-pane label="教研室" name="department" />
        <el-tab-pane label="班级" name="classInfo" />
      </el-tabs>

      <el-table v-loading="loading" :data="filteredRows" border>
        <el-table-column
            v-for="column in activeConfig.columns"
            :key="column.prop"
            :prop="column.prop"
            :label="column.label"
            :width="column.width"
        >
          <template v-if="column.type === 'college'" #default="{ row }">
            {{ collegeNameMap[row.collegeId] || '-' }}
          </template>
          <template v-else-if="column.type === 'major'" #default="{ row }">
            {{ majorNameMap[row.majorId] || '-' }}
          </template>
        </el-table-column>
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
        :title="dialogMode === 'create' ? `新增${activeConfig.title}` : `编辑${activeConfig.title}`"
        width="520px"
    >
      <el-form ref="formRef" :model="form" :rules="activeConfig.rules" label-width="96px">
        <el-form-item
            v-for="field in activeConfig.fields"
            :key="field.prop"
            :label="field.label"
            :prop="field.prop"
        >
          <el-select
              v-if="field.type === 'college'"
              v-model="form[field.prop]"
              placeholder="请选择学院"
              style="width: 100%"
          >
            <el-option v-for="item in colleges" :key="item.id" :label="item.collegeName" :value="item.id" />
          </el-select>
          <el-select
              v-else-if="field.type === 'major'"
              v-model="form[field.prop]"
              placeholder="请选择专业"
              style="width: 100%"
          >
            <el-option v-for="item in majors" :key="item.id" :label="item.majorName" :value="item.id" />
          </el-select>
          <el-input-number
              v-else-if="field.type === 'number'"
              v-model="form[field.prop]"
              :min="1"
              style="width: 100%"
          />
          <el-input v-else v-model="form[field.prop]" :placeholder="`请输入${field.label}`" />
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
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { createCollege, listColleges, removeCollege, updateCollege } from '../../api/college'
import { createMajor, listMajors, removeMajor, updateMajor } from '../../api/major'
import { createDepartment, listDepartments, removeDepartment, updateDepartment } from '../../api/department'
import { createClassInfo, listClassInfos, removeClassInfo, updateClassInfo } from '../../api/classInfo'

const activeTab = ref('college')
const keyword = ref('')
const status = ref('全部')
const loading = ref(false)
const dialogVisible = ref(false)
const dialogMode = ref('create')
const formRef = ref()
const form = reactive({})

const colleges = ref([])
const majors = ref([])
const departments = ref([])
const classInfos = ref([])

const configs = {
  college: {
    title: '学院',
    rows: colleges,
    list: listColleges,
    create: createCollege,
    update: updateCollege,
    remove: removeCollege,
    searchPlaceholder: '搜索学院编号或名称',
    keywordFields: ['collegeCode', 'collegeName'],
    columns: [
      { prop: 'collegeCode', label: '学院编号', width: 140 },
      { prop: 'collegeName', label: '学院名称' },
      { prop: 'description', label: '学院简介' }
    ],
    fields: [
      { prop: 'collegeCode', label: '学院编号' },
      { prop: 'collegeName', label: '学院名称' },
      { prop: 'description', label: '学院简介' }
    ],
    defaults: { collegeCode: '', collegeName: '', description: '' },
    rules: {
      collegeCode: [{ required: true, message: '请输入学院编号', trigger: 'blur' }],
      collegeName: [{ required: true, message: '请输入学院名称', trigger: 'blur' }]
    }
  },
  major: {
    title: '专业',
    rows: majors,
    list: listMajors,
    create: createMajor,
    update: updateMajor,
    remove: removeMajor,
    searchPlaceholder: '搜索专业编号或名称',
    keywordFields: ['majorCode', 'majorName'],
    columns: [
      { prop: 'collegeId', label: '所属学院', type: 'college' },
      { prop: 'majorCode', label: '专业编号', width: 140 },
      { prop: 'majorName', label: '专业名称' },
      { prop: 'schoolingYears', label: '学制', width: 90 },
      { prop: 'degreeType', label: '学位类型', width: 120 }
    ],
    fields: [
      { prop: 'collegeId', label: '所属学院', type: 'college' },
      { prop: 'majorCode', label: '专业编号' },
      { prop: 'majorName', label: '专业名称' },
      { prop: 'schoolingYears', label: '学制', type: 'number' },
      { prop: 'degreeType', label: '学位类型' }
    ],
    defaults: { collegeId: null, majorCode: '', majorName: '', schoolingYears: 4, degreeType: '' },
    rules: {
      collegeId: [{ required: true, message: '请选择所属学院', trigger: 'change' }],
      majorCode: [{ required: true, message: '请输入专业编号', trigger: 'blur' }],
      majorName: [{ required: true, message: '请输入专业名称', trigger: 'blur' }],
      schoolingYears: [{ required: true, message: '请输入学制', trigger: 'change' }]
    }
  },
  department: {
    title: '教研室',
    rows: departments,
    list: listDepartments,
    create: createDepartment,
    update: updateDepartment,
    remove: removeDepartment,
    searchPlaceholder: '搜索教研室编号或名称',
    keywordFields: ['departmentCode', 'departmentName'],
    columns: [
      { prop: 'collegeId', label: '所属学院', type: 'college' },
      { prop: 'departmentCode', label: '教研室编号', width: 140 },
      { prop: 'departmentName', label: '教研室名称' },
      { prop: 'officeLocation', label: '办公地点', width: 160 }
    ],
    fields: [
      { prop: 'collegeId', label: '所属学院', type: 'college' },
      { prop: 'departmentCode', label: '教研室编号' },
      { prop: 'departmentName', label: '教研室名称' },
      { prop: 'officeLocation', label: '办公地点' }
    ],
    defaults: { collegeId: null, departmentCode: '', departmentName: '', officeLocation: '' },
    rules: {
      collegeId: [{ required: true, message: '请选择所属学院', trigger: 'change' }],
      departmentCode: [{ required: true, message: '请输入教研室编号', trigger: 'blur' }],
      departmentName: [{ required: true, message: '请输入教研室名称', trigger: 'blur' }]
    }
  },
  classInfo: {
    title: '班级',
    rows: classInfos,
    list: listClassInfos,
    create: createClassInfo,
    update: updateClassInfo,
    remove: removeClassInfo,
    searchPlaceholder: '搜索班级编号或名称',
    keywordFields: ['classCode', 'className'],
    columns: [
      { prop: 'majorId', label: '所属专业', type: 'major' },
      { prop: 'classCode', label: '班级编号', width: 140 },
      { prop: 'className', label: '班级名称' },
      { prop: 'entranceYear', label: '入学年份', width: 120 }
    ],
    fields: [
      { prop: 'majorId', label: '所属专业', type: 'major' },
      { prop: 'classCode', label: '班级编号' },
      { prop: 'className', label: '班级名称' },
      { prop: 'entranceYear', label: '入学年份', type: 'number' }
    ],
    defaults: { majorId: null, classCode: '', className: '', entranceYear: new Date().getFullYear() },
    rules: {
      majorId: [{ required: true, message: '请选择所属专业', trigger: 'change' }],
      classCode: [{ required: true, message: '请输入班级编号', trigger: 'blur' }],
      className: [{ required: true, message: '请输入班级名称', trigger: 'blur' }],
      entranceYear: [{ required: true, message: '请输入入学年份', trigger: 'change' }]
    }
  }
}

const activeConfig = computed(() => configs[activeTab.value])
const collegeNameMap = computed(() => Object.fromEntries(colleges.value.map((item) => [item.id, item.collegeName])))
const majorNameMap = computed(() => Object.fromEntries(majors.value.map((item) => [item.id, item.majorName])))

const filteredRows = computed(() => {
  return activeConfig.value.rows.value.filter((item) => {
    const matchKeyword = !keyword.value || activeConfig.value.keywordFields.some((field) => {
      return String(item[field] || '').includes(keyword.value)
    })
    const matchStatus = status.value === '全部' || String(item.status ?? 1) === status.value
    return matchKeyword && matchStatus
  })
})

watch(activeTab, () => {
  keyword.value = ''
  status.value = '全部'
})

onMounted(() => {
  refreshAll()
})

async function refreshAll() {
  loading.value = true
  try {
    const [collegeRows, majorRows, departmentRows, classRows] = await Promise.all([
      listColleges(),
      listMajors(),
      listDepartments(),
      listClassInfos()
    ])
    colleges.value = collegeRows
    majors.value = majorRows
    departments.value = departmentRows
    classInfos.value = classRows
  } finally {
    loading.value = false
  }
}

function resetForm(data = activeConfig.value.defaults) {
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
    await activeConfig.value.create(form)
  } else {
    await activeConfig.value.update(form.id, form)
  }
  ElMessage.success('保存成功')
  dialogVisible.value = false
  await refreshAll()
}

async function handleRemove(row) {
  await ElMessageBox.confirm(`确定停用该${activeConfig.value.title}吗？`, '操作确认', {
    type: 'warning',
    confirmButtonText: '停用',
    cancelButtonText: '取消'
  })
  await activeConfig.value.remove(row.id)
  ElMessage.success('停用成功')
  await refreshAll()
}
</script>
