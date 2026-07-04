<template>
  <div class="page">
    <div>
      <h1 class="page-title">操作日志</h1>
      <div class="page-description">查看登录、维护、选课、退课和成绩录入等关键操作记录。</div>
    </div>

    <el-card>
      <el-form class="filter-form" :model="filters" inline>
        <el-form-item label="模块">
          <el-select v-model="filters.moduleName" clearable placeholder="全部模块" style="width: 160px">
            <el-option label="登录认证" value="登录认证" />
            <el-option label="账号管理" value="账号管理" />
            <el-option label="学生管理" value="学生管理" />
            <el-option label="教师管理" value="教师管理" />
            <el-option label="课程管理" value="课程管理" />
            <el-option label="选课管理" value="选课管理" />
            <el-option label="成绩管理" value="成绩管理" />
          </el-select>
        </el-form-item>
        <el-form-item label="操作">
          <el-select v-model="filters.operationType" clearable placeholder="全部操作" style="width: 170px">
            <el-option label="登录" value="LOGIN" />
            <el-option label="退出" value="LOGOUT" />
            <el-option label="新增" value="INSERT" />
            <el-option label="修改" value="UPDATE" />
            <el-option label="停用" value="DISABLE" />
            <el-option label="选课" value="SELECT_COURSE" />
            <el-option label="退课" value="DROP_COURSE" />
            <el-option label="成绩录入" value="UPDATE_SCORE" />
          </el-select>
        </el-form-item>
        <el-form-item label="时间">
          <el-date-picker
            v-model="dateRange"
            type="datetimerange"
            value-format="YYYY-MM-DD HH:mm:ss"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            style="width: 360px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">查询</el-button>
          <el-button @click="reset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="rows" border empty-text="暂无操作日志">
        <el-table-column prop="createdAt" label="操作时间" width="190" />
        <el-table-column prop="username" label="用户" width="120">
          <template #default="{ row }">{{ row.username || '-' }}</template>
        </el-table-column>
        <el-table-column prop="moduleName" label="模块" width="120" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-tag effect="plain">{{ operationText(row.operationType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="220" />
        <el-table-column prop="targetTable" label="数据表" width="130" />
        <el-table-column prop="targetId" label="目标ID" width="100">
          <template #default="{ row }">{{ row.targetId || '-' }}</template>
        </el-table-column>
        <el-table-column prop="ipAddress" label="IP地址" width="140" />
      </el-table>

      <div class="pagination-row">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="refresh"
          @current-change="refresh"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { listOperationLogs } from '../../api/operationLog'

const loading = ref(false)
const rows = ref([])
const dateRange = ref([])
const filters = reactive({
  moduleName: '',
  operationType: ''
})
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

onMounted(refresh)

async function refresh() {
  loading.value = true
  try {
    const data = await listOperationLogs({
      moduleName: filters.moduleName || undefined,
      operationType: filters.operationType || undefined,
      startTime: dateRange.value?.[0],
      endTime: dateRange.value?.[1],
      page: pagination.page,
      size: pagination.size
    })
    rows.value = data.records || []
    pagination.total = Number(data.total || 0)
  } finally {
    loading.value = false
  }
}

function search() {
  pagination.page = 1
  refresh()
}

function reset() {
  filters.moduleName = ''
  filters.operationType = ''
  dateRange.value = []
  pagination.page = 1
  refresh()
}

function operationText(value) {
  return {
    LOGIN: '登录',
    LOGOUT: '退出',
    INSERT: '新增',
    UPDATE: '修改',
    DISABLE: '停用',
    SELECT_COURSE: '选课',
    DROP_COURSE: '退课',
    UPDATE_SCORE: '成绩录入'
  }[value] || value
}
</script>

<style scoped>
.filter-form {
  margin-bottom: 16px;
}

.pagination-row {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>

