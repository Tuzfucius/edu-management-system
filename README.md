# 教务管理系统


本项目是《Java应用课程设计》的教务管理系统，后端采用 **Spring Boot 4.1.0 + Java 17 + Maven**，前端采用 **Vue 3 + Element Plus**，数据库使用 **MySQL**。

## 功能总览

系统面向学校教务管理场景，支持**管理员、教师、学生**三类角色登录，核心功能包括：

### 基础数据管理
- **组织结构**：学院、专业、教研室、班级的 CRUD 维护
- **人员管理**：教师信息、学生信息的 CRUD 维护，支持模拟数据导入
- **课程管理**：课程信息 CRUD，教学任务（任课安排）分配
- **系统用户**：统一用户管理，角色权限分配

### 教务业务
- **学生选课**：学生自主选课 / 退课，选课容量控制
- **教师任课**：教师与课程关联，教学任务安排
- **成绩管理**：教师录入学生成绩，学生查询本人成绩
- **师生关联**：教师查看名下学生列表

### 权限与安全
- **Session 登录认证**：统一 Session 鉴权
- **角色权限拦截**：强制角色鉴权（`@RequireRole` 注解 + 拦截器）
- **越权访问防护**：按角色过滤可访问的 API 和页面

### 统计报表
- 学院学生人数、课程选课人数统计
- 成绩分布分析（ECharts 图形报表）
- 教师教学工作量统计报表

### 系统运维
- 操作日志记录与查询
- 请求日志追踪（`requestId` 贯穿日志）

## 技术栈

| 层级 | 技术 |
|---|---|
| 后端框架 | Spring Boot 4.1.0、Java 17、Maven |
| 数据库 | MySQL 8.0 |
| 数据访问 | Spring JDBC / JdbcTemplate |
| 前端框架 | Vue 3 (Composition API + `<script setup>`) |
| 构建工具 | Vite 8 |
| UI 组件 | Element Plus |
| 图表 | ECharts 6 |
| HTTP 客户端 | Axios（统一拦截、自动鉴权跳转） |
| 部署 | Docker / Docker Compose |

## 项目目录结构

```text
.
├─ docker/                   Docker 部署配置
│  ├─ backend/Dockerfile     后端多阶段构建（Maven → JRE）
│  ├─ frontend/Dockerfile    前端多阶段构建（Node → Nginx）
│  └─ nginx/nginx.conf       Nginx 反向代理配置
├─ docs/                     需求、整体规划、数据库设计等课程文档
├─ sql/                      MySQL 初始化脚本（建库、建表、插入示例数据）
├─ src/
│  ├─ main/java/             Spring Boot 后端 Java 源码
│  │  └─ com/tzufucius/edu/edumanagementsystem/
│  │     ├─ controller/       Web 控制器
│  │     ├─ service/          业务逻辑层
│  │     ├─ dao/              JdbcTemplate 数据访问层
│  │     ├─ entity/           数据库表对应实体
│  │     ├─ dto/vo/           页面展示对象
│  │     ├─ config/           Web 配置、拦截器配置
│  │     ├─ auth/             登录认证与角色权限
│  │     ├─ common/           通用返回结构、分页对象、工具类
│  │     └─ exception/        全局异常处理
│  ├─ main/resources/         Spring Boot 配置（application.yaml）
│  └─ test/java/              后端测试代码
├─ frontend/                  Vue 3 + Element Plus 前端工程
│  └─ src/
│     ├─ views/               页面组件
│     │  ├─ admin/            管理员：控制台/人员/课程/教务/报表/日志
│     │  ├─ teacher/          教师：课程/成绩/报表
│     │  ├─ student/          学生：选课/成绩
│     │  └─ error/            通用：401/404
│     ├─ api/                 API 接口封装
│     ├─ router/              路由配置
│     ├─ utils/               工具函数（axios 实例、样式）
│     └─ data/                模拟数据
├─ docker-compose.yml         Docker Compose 服务编排
├─ .env.example               环境变量模板
├─ .dockerignore              构建上下文排除规则
├─ pom.xml                    Maven 项目配置
└─ README.md                  项目说明
```

## 快速启动

### 方式一：Docker 部署（推荐）

保证本机已安装 **Docker** 和 **Docker Compose**。

```bash
# 1. 复制环境变量文件
cp .env.example .env

# 2. 构建并启动所有服务
docker-compose up --build
```

首次构建耗时约 3-5 分钟（下载 Maven 依赖 + Node 依赖）。启动后访问：

- **前端页面**：http://localhost （自动重定向到 `/edu/`）
- **后端 API**：http://localhost:8080/edu/api

> **端口说明**：MySQL 宿主机映射到 `3307` 端口，避免与本机已有 MySQL 冲突。后端 `8080` 端口可在 `docker-compose.yml` 中按需注释。

#### 服务架构

```
浏览器 ──:80──→ Nginx ──/edu/api/*──→ Backend(:8080) ──jdbc──→ MySQL(:3306)
                 │
                 └── /edu/* ──→ 静态文件（前端 SPA）
```

- **Nginx**：统一入口，托管前端静态文件，反向代理 API 请求
- **Backend**：Spring Boot 应用，等待 MySQL 健康检查通过后启动
- **MySQL**：启动时自动执行 `sql/` 下的初始化脚本（建库、建表、插入示例数据）

#### 常用命令

```bash
# 启动（后台）
docker-compose up -d

# 查看日志
docker-compose logs -f

# 停止
docker-compose down

# 停止并删除数据卷（清空数据库）
docker-compose down -v
```

### 方式二：本地开发

**前置条件**：Java 17、Node.js 20+、MySQL 8.0

```bash
# 1. 初始化数据库
#    按顺序执行 sql/ 下的三个脚本（可用 Navicat、DataGrip 或命令行）
#    数据库名称：edu_management_system

# 2. 启动后端
./mvnw spring-boot:run

# 3. 启动前端
cd frontend
npm install
npm run dev
```

本地开发时前端访问 http://localhost:5173，Vite 自动代理 `/edu/api` 请求到后端 `8080` 端口。

## 持续集成（CI）

每次推送或发起 Pull Request 到 `main` 分支时，GitHub Actions 自动执行以下并行检查：

| Job | 职责 |
|---|---|
| **backend** | Maven 构建 + 21 个测试用例（连接真实 MySQL service 容器） |
| **frontend** | npm ci + npm run build（验证前端可构建） |
| **docker** | Docker Buildx 构建 backend 和 frontend 镜像（验证 Dockerfile 无错误） |


CI 配置文件：`.github/workflows/ci.yml`

## 数据库

当前系统使用 11 张核心业务表和 1 张扩展日志表：

- **权限用户**：`sys_user`
- **组织结构**：`college`、`major`、`department`、`class_info`
- **人员信息**：`student`、`teacher`
- **课程业务**：`course`、`teaching_task`、`student_course`、`teacher_student`
- **扩展日志**：`operation_log`

关键关系：

```text
college → major → class_info → student
college → department → teacher
teacher + course → teaching_task
student + teaching_task → student_course
teacher + student → teacher_student
```

SQL 脚本位于 `sql/` 目录下：
- `01_create_database.sql` — 建库
- `02_create_tables.sql` — 建表
- `03_insert_sample_data.sql` — 插入示例数据（含初始管理員账号）
