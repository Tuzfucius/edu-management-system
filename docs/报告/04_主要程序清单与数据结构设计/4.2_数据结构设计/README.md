# 4.2 数据结构设计

本节说明数据库逻辑结构、物理表结构、约束设计和事务设计。数据库采用 MySQL，字符集使用 utf8mb4，核心业务表共 12 张。设计目标是在满足课程任务要求的基础上，使组织结构、人员信息、课程任务、选课成绩、指导关系和操作日志之间保持清晰关联。

## 4.2.1 逻辑结构设计（核心实体 E-R 关系与关联分析）

系统逻辑结构以教务业务实体为中心。组织结构分为两条主线：第一条是学院、专业、班级、学生，表示学生培养单位；第二条是学院、教研室、教师，表示教师教学单位。课程业务以课程为基础，但课程本身只是资源定义，不能直接代表某学期的教学活动，因此通过任课任务连接课程与教师。学生不直接选择课程表中的抽象课程，而是选择具体任课任务，选课结果和成绩保存于学生选课成绩表。教师指导学生关系独立建表，避免与任课关系混淆。用户表独立保存账号、密码、角色和状态，再通过学生或教师表与人员身份绑定。

核心关系可以概括为：一个学院下有多个专业和多个教研室；一个专业下有多个班级；一个班级下有多个学生；一个教研室下有多个教师；一个教师可以承担多个任课任务，一门课程也可以形成多个任课任务；一个学生可以选择多个任课任务，一个任课任务也可以被多个学生选择；教师与学生之间还可以通过指导关系形成多对多联系。这样的逻辑结构避免了冗余字段。例如学生表不直接保存学院和专业，而是通过班级追溯到专业和学院；成绩不放在学生表或课程表，而是放在学生选课成绩表中，因为成绩只有在“某学生选择某开课任务”的上下文中才有意义。该 E-R 结构能够支撑基础信息管理、任课管理、选课退课、成绩录入、报表统计和日志追踪等功能。

## 4.2.2 物理结构设计（12 张 MySQL 业务物理数据表结构设计）

物理结构设计遵循统一主键、业务编号唯一、外键表达关联、状态字段控制有效性、时间字段记录创建更新的原则。所有主表均采用 `BIGINT` 自增主键。账号、学号、工号、课程编号、学院编号、专业编号、教研室编号、班级编号等具有现实含义的业务编号设置唯一约束。涉及组织或业务从属关系的字段设置外键，例如专业关联学院、班级关联专业、学生关联班级和用户、教师关联教研室和用户、任课任务关联课程和教师、选课成绩关联学生和任课任务。以下表格为报告中的规范化字段设计摘要。

| 表名 | 字段与类型摘要 | 主键与主要约束 | 用途 |
| --- | --- | --- | --- |
| sys_user | id BIGINT，username VARCHAR(50)，password VARCHAR(100)，role VARCHAR(20)，status TINYINT，last_login_at DATETIME，created_at DATETIME，updated_at DATETIME | id 主键，username 唯一，role 限定 ADMIN/TEACHER/STUDENT，status 限定 0/1 | 保存登录账号、角色和账号状态 |
| college | id BIGINT，college_code VARCHAR(30)，college_name VARCHAR(100)，description VARCHAR(255)，status TINYINT，created_at DATETIME，updated_at DATETIME | id 主键，college_code 唯一，status 限定 0/1 | 保存学院基础信息 |
| major | id BIGINT，college_id BIGINT，major_code VARCHAR(30)，major_name VARCHAR(100)，schooling_years TINYINT，degree_type VARCHAR(50)，status TINYINT，created_at DATETIME，updated_at DATETIME | id 主键，major_code 唯一，college_id 外键 | 保存专业信息并关联学院 |
| department | id BIGINT，college_id BIGINT，department_code VARCHAR(30)，department_name VARCHAR(100)，office_location VARCHAR(100)，status TINYINT，created_at DATETIME，updated_at DATETIME | id 主键，department_code 唯一，college_id 外键 | 保存教研室信息并关联学院 |
| class_info | id BIGINT，major_id BIGINT，class_code VARCHAR(30)，class_name VARCHAR(100)，entrance_year SMALLINT，status TINYINT，created_at DATETIME，updated_at DATETIME | id 主键，class_code 唯一，major_id 外键 | 保存班级信息并关联专业 |
| student | id BIGINT，user_id BIGINT，class_id BIGINT，student_no VARCHAR(30)，student_name VARCHAR(50)，gender CHAR(1)，birth_date DATE，phone VARCHAR(20)，email VARCHAR(100)，enrollment_year SMALLINT，status TINYINT，created_at DATETIME，updated_at DATETIME | id 主键，user_id 唯一外键，student_no 唯一，class_id 外键，gender 限定 M/F | 保存学生资料和账号绑定关系 |
| teacher | id BIGINT，user_id BIGINT，department_id BIGINT，teacher_no VARCHAR(30)，teacher_name VARCHAR(50)，gender CHAR(1)，title VARCHAR(50)，phone VARCHAR(20)，email VARCHAR(100)，status TINYINT，created_at DATETIME，updated_at DATETIME | id 主键，user_id 唯一外键，teacher_no 唯一，department_id 外键，gender 限定 M/F | 保存教师资料和账号绑定关系 |
| course | id BIGINT，course_code VARCHAR(30)，course_name VARCHAR(100)，credit DECIMAL(3,1)，total_hours INT，course_type VARCHAR(20)，exam_type VARCHAR(20)，status TINYINT，created_at DATETIME，updated_at DATETIME | id 主键，course_code 唯一，credit 大于 0，status 限定 0/1 | 保存课程基础信息 |
| teaching_task | id BIGINT，course_id BIGINT，teacher_id BIGINT，semester VARCHAR(20)，weekday TINYINT，start_section TINYINT，end_section TINYINT，weeks VARCHAR(50)，classroom VARCHAR(100)，capacity INT，selected_count INT，task_status TINYINT，created_at DATETIME，updated_at DATETIME | id 主键，course_id 和 teacher_id 外键，weekday 为 1 到 7，start_section 不大于 end_section，selected_count 不超过 capacity | 保存具体开课和任课安排 |
| student_course | id BIGINT，student_id BIGINT，teaching_task_id BIGINT，select_time DATETIME，score DECIMAL(5,2)，grade_status TINYINT，status TINYINT，remark VARCHAR(255)，created_at DATETIME，updated_at DATETIME | id 主键，student_id 和 teaching_task_id 组合唯一，score 为 0 到 100，grade_status 与 status 限定 0/1 | 保存选课记录、退课状态和成绩 |
| teacher_student | id BIGINT，teacher_id BIGINT，student_id BIGINT，guide_type VARCHAR(50)，start_date DATE，end_date DATE，status TINYINT，created_at DATETIME，updated_at DATETIME | id 主键，teacher_id 和 student_id 组合唯一，结束日期不早于开始日期 | 保存教师指导学生关系 |
| operation_log | id BIGINT，user_id BIGINT，module_name VARCHAR(50)，operation_type VARCHAR(30)，target_table VARCHAR(50)，target_id BIGINT，description VARCHAR(255)，ip_address VARCHAR(50)，created_at DATETIME | id 主键，user_id 外键且用户删除时置空 | 保存系统操作日志 |

该物理结构能够覆盖课程设计中要求的学院、专业、教研室、教师、班级、学生、课程、教师任课时间、学生选课成绩、教师指导学生、权限用户和统计报表支撑。报表不单独建表，而是通过这些业务表实时聚合得到。

## 4.2.3 数据一致性与约束设计（唯一约束 / 外键关联 / 状态字段 / 删除限制）

数据一致性设计主要依赖唯一约束、外键关联、检查约束、状态字段和删除限制。唯一约束用于防止业务编号重复，例如登录账号、学院编号、专业编号、教研室编号、班级编号、学号、工号和课程编号。学生选课成绩表对学生编号和任课任务编号建立组合唯一约束，防止同一学生重复选择同一开课任务。教师指导学生表对教师编号和学生编号建立组合唯一约束，防止重复建立相同指导关系。这些约束不仅服务于数据库正确性，也能简化后端业务判断。

外键关联用于表达实体依赖关系，并通过删除限制保护历史数据。专业依赖学院，班级依赖专业，学生依赖班级和用户，教师依赖教研室和用户，任课任务依赖课程和教师，选课成绩依赖学生和任课任务。对于这些重要关联，数据库采用限制删除策略，避免误删上游记录导致下游业务数据孤立。状态字段则用于逻辑停用，主要表中 `status` 或 `task_status` 表示正常、停用、已退课或已结课等业务状态。这样既能避免物理删除破坏历史，又能让列表查询和统计报表排除无效数据。检查约束用于限制角色、性别、状态、星期、节次、容量、成绩等字段范围。通过这些约束组合，系统能够在应用层校验之外增加数据库底线，降低异常数据进入核心表的可能性。

## 4.2.4 事务处理设计（选课 / 退课 / 成绩录入 / 用户与学生信息联动）

事务处理设计用于保证多个相关数据更新要么全部成功，要么全部失败。选课事务至少包含两个动作：写入或恢复学生选课记录，以及增加任课任务已选人数。如果只写入选课记录而没有更新已选人数，容量显示会失真；如果只增加人数而没有选课记录，学生成绩和课程名单会缺失。因此这两个动作应被视为一个原子业务单元。退课事务与选课相反，需要把选课记录状态改为退课，并减少任课任务已选人数，同时要防止人数减为负数。容量已满、重复选课或任务不可选时，应在事务写入前终止。

成绩录入事务通常只更新一条选课成绩记录，但仍需要保证成绩字段和成绩状态一致。若成绩更新成功而成绩状态仍为未录入，报表统计和学生查询会出现偏差；若状态改为已录入而成绩为空，也会影响 GPA 和成绩分布。用户与学生信息联动事务则体现在新增学生或教师时，人员记录与登录账号应保持一致，账号角色应与人员类型匹配。当前项目课程设计阶段的事务复杂度适中，重点是明确事务边界和失败回滚原则。后续若系统扩展到高并发选课，还需要引入更严格的并发控制，例如行级锁、乐观版本号或外部原子计数，以保证容量在并发场景下仍准确。
