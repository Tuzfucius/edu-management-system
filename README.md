# 教务管理系统

本项目是《Java应用课程设计》的教务管理系统，采用 Spring Boot + Maven + MySQL 技术栈。当前阶段重点是完成数据库设计、项目文件架构和后续智能体开发上下文，为登录、权限、基础信息维护、选课、成绩录入和统计报表功能打基础。

## 项目目标

系统面向学校教务管理场景，至少覆盖以下能力：

- 管理学院、专业、教研室、班级、教师、学生、课程等基础信息。
- 支持管理员、教师、学生三类角色的基础权限管理。
- 支持教师任课、学生选课、成绩录入、学生成绩查询。
- 支持基础统计报表和图形分析报表，例如学院学生人数、课程选课人数、成绩分布等。
- 支持模拟数据导入，便于开发、演示、课程报告和答辩。

## 技术栈

- 后端：Spring Boot 4.1.0、Java 17、Maven
- 数据库：MySQL
- 数据访问：Spring JDBC / JdbcTemplate
- 页面方案：前端尚未最终确定，当前规划优先采用 Thymeleaf + Bootstrap + ECharts，避免过早引入复杂前后端分离架构。
- 开发环境：Windows 11；如需 Python 辅助脚本，默认 conda 环境为 `low_numpy`。

## 当前目录架构

```text
.
├─ docs/                  需求、整体规划、数据库设计等课程文档
├─ sql/                   手动执行的 MySQL 初始化脚本
├─ src/main/java/         Spring Boot 后端 Java 源码
├─ src/main/resources/    Spring Boot 配置、静态资源、模板
├─ src/test/java/         后端测试代码
├─ pom.xml                Maven 项目配置
└─ README.md              项目说明和智能体上下文入口
```

后续后端包结构建议放在 `com.tzufucius.edu.edumanagementsystem` 下：

```text
controller   Web 控制器，处理页面和接口请求
service      业务逻辑，负责权限判断、流程校验和事务边界
dao          JdbcTemplate 数据访问层
entity       数据库表对应实体
dto          页面展示或查询结果组合对象
config       Web、拦截器、全局配置
interceptor  登录和角色权限拦截
common       通用返回结构、分页对象、常量
exception    全局异常处理
```

## 数据库脚本执行

当前采用手动执行 SQL 的基础方式，不使用 Flyway，也不依赖 Spring Boot 启动时自动初始化。

执行顺序：

```bash
mysql -u root -p < sql/01_create_database.sql
mysql -u root -p < sql/02_create_tables.sql
mysql -u root -p < sql/03_insert_sample_data.sql
```

如果使用 Navicat、DataGrip 或 MySQL Workbench，也按同样顺序逐个执行。

数据库名称为 `edu_management_system`。表结构以 `docs/数据库计划.md` 为主要依据；如果整体计划与具体数据库计划存在出入，以具体数据库计划为准。

## 数据库核心表

当前 SQL 脚本包含 11 张核心业务表和 1 张扩展日志表：

- 权限用户：`sys_user`
- 组织结构：`college`、`major`、`department`、`class_info`
- 人员信息：`student`、`teacher`
- 课程业务：`course`、`teaching_task`、`student_course`、`teacher_student`
- 扩展日志：`operation_log`

关键关系：

```text
college -> major -> class_info -> student
college -> department -> teacher
teacher + course -> teaching_task
student + teaching_task -> student_course
teacher + student -> teacher_student
```

## 智能体开发上下文

后续智能体开发时应遵守以下约定：

- 优先阅读 `README.md`、`docs/README.md`、`docs/整体计划.md`、`docs/数据库计划.md`、`docs/java任务书.txt`。
- 涉及数据库表结构时，以 `docs/数据库计划.md` 和 `sql/02_create_tables.sql` 为准。
- 涉及课程评分点时，以 `docs/java任务书.txt` 为准。
- 使用 CodeGraph 进行结构化代码理解。本项目已初始化 CodeGraph；新增或大幅调整 Java 代码后，可执行 `codegraph index` 更新索引。
- 小任务完成后提交清晰的 git commit；大型功能应先从 `main` 创建 `agent/` 前缀的新分支再开发。
- 新增目录时，应补充该目录下的 Markdown 说明文件，解释目录职责和维护规则。
- 后续实现应优先打通最小闭环：MySQL 连接、登录、角色跳转、学生列表查询、学生 CRUD。

## 后续开发顺序

1. 配置 `application.yaml` 连接 MySQL。
2. 实现登录页面、Session 登录状态和角色权限拦截。
3. 实现学生、教师、课程三个核心 CRUD。
4. 实现学院、专业、班级、教研室等基础数据维护。
5. 实现任课安排、学生选课、教师录入成绩。
6. 实现统计报表和 ECharts 图形分析。
7. 完成页面统一、异常处理、数据校验、操作日志和课程报告截图。
