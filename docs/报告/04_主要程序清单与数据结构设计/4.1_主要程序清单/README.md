# 4.1 主要程序清单（类结构与接口定义）

本节采用接口清单方式说明主要程序结构。清单只描述数据成员、函数名、返回值、参数和调用关系，不展示函数体。为了与项目实际保持一致，部分目录中给出的理想类名被映射到当前项目已有类；这既能体现课程设计要求，也避免把未实现的独立类写成已完成代码。

## 4.1.1 身份认证控制器与安全拦截器（AuthController / AuthService / AuthInterceptor / WebConfig）

身份认证模块的当前实际入口是 `AuthController`，其主要数据成员包括认证业务服务对象和操作日志服务对象。认证服务职责在当前项目中主要由 `AcademicBusinessService` 承担，负责根据账号密码查询用户、校验账号状态、更新最后登录时间并生成登录用户视图。目录中提到的 `AuthService` 可理解为后续拆分后的认证专用服务，`AuthInterceptor` 与 `WebConfig` 可理解为后续用于统一后端登录拦截和角色校验的配置组件。当前项目权限控制主要由登录会话、前端路由守卫、角色菜单和业务接口参数共同完成，尚未形成独立后端拦截器链。

| 类或职责 | 数据成员 | 函数名 | 返回值 | 参数 | 调用关系说明 |
| --- | --- | --- | --- | --- | --- |
| AuthController | academicBusinessService、operationLogService | login | Result 登录用户信息 | 登录请求、会话、请求对象 | 前端登录页调用，控制器调用业务服务校验账号，成功后写入会话并记录日志 |
| AuthController | academicBusinessService、operationLogService | me | Result 登录用户信息 | 会话 | 前端刷新页面时调用，用于恢复当前登录用户 |
| AuthController | operationLogService | logout | Result 空结果 | 会话、请求对象 | 前端退出登录时调用，记录退出日志并清理会话 |
| 设计职责 AuthService | 用户数据访问服务 | authenticate | 登录用户信息 | 用户名、密码 | 后续可从综合业务服务中拆出，集中处理认证逻辑 |
| 设计职责 AuthInterceptor | 会话读取器、角色规则 | preHandle | 是否放行 | 请求、响应、处理器 | 后续可统一拦截未登录和越权请求 |
| 设计职责 WebConfig | 拦截器对象 | addInterceptors | 无返回 | 拦截器注册器 | 后续可配置拦截路径和白名单 |

该模块的接口调用链为：前端 `auth.js` 发起登录请求，`AuthController` 接收请求，调用 `AcademicBusinessService` 完成用户验证，服务层调用 `AcademicBusinessDao` 查询 `sys_user` 并更新登录时间，控制器把登录结果写入会话并返回统一结果。若发生用户名密码错误或账号停用，服务层返回业务错误并由统一异常机制转化为失败响应。该清单说明了认证模块的职责边界，也指出当前项目为了课程设计效率采用了综合服务类，后续若继续演进，应优先拆分认证服务和后端拦截器。

## 4.1.2 学生与教师信息服务类（StudentController / StudentService / StudentDao / TeacherService）

学生与教师信息服务类用于维护人员基础资料和账号关联关系。当前项目中，学生和教师控制器分别为 `StudentController` 与 `TeacherController`，业务逻辑主要由 `AcademicBusinessService` 承担，数据访问由 `AcademicBusinessDao` 承担。基础学生和教师信息不是孤立记录，学生必须关联用户账号和班级，教师必须关联用户账号和教研室，因此新增或修改操作需要同时考虑账号角色、组织归属、业务编号唯一性和状态字段。目录中写到的 `StudentService`、`StudentDao`、`TeacherService` 在当前项目中并未作为独立文件全部拆开，而是以综合业务服务方式实现。

| 类或职责 | 数据成员 | 函数名 | 返回值 | 参数 | 调用关系说明 |
| --- | --- | --- | --- | --- | --- |
| StudentController | service、operationLogService | list | Result 学生列表 | 无 | 管理员页面调用，返回学生及班级、专业、学院关联信息 |
| StudentController | service | get | Result 学生详情 | 学生编号 | 用于编辑弹窗或详情页面加载数据 |
| StudentController | service | getByUser | Result 学生详情 | 用户编号 | 学生端根据登录用户定位学生身份 |
| StudentController | service、operationLogService | create | Result 空结果 | 学生表单、会话、请求对象 | 新增学生并记录操作日志 |
| StudentController | service、operationLogService | update | Result 空结果 | 学生编号、学生表单、会话、请求对象 | 修改学生资料并记录日志 |
| StudentController | service、operationLogService | delete | Result 空结果 | 学生编号、会话、请求对象 | 逻辑停用学生资料 |
| TeacherController | service、operationLogService | list/get/create/update/delete | Result | 教师编号或教师表单 | 维护教师资料、账号绑定和教研室归属 |

调用关系上，前端人员管理页面通过学生、教师 API 调用控制器。控制器不直接检查所有业务字段，而是把表单映射数据交给服务层。服务层检查学号、工号、姓名、用户、组织归属等关键字段，必要时转换为数值类型，再调用 Dao 层执行关联查询或更新。操作成功后，日志服务记录模块名称、操作类型、目标表和目标记录。该模块的测试重点包括学号唯一、工号唯一、缺少班级或教研室、账号角色不匹配、停用后不应参与正常统计等情况。通过学生与教师服务类，系统建立了后续任课、选课和成绩管理所需的人员基础。

## 4.1.3 课程、任课与选课业务逻辑类（CourseService / TeachingTaskService / SelectionService）

课程、任课与选课业务逻辑类构成教学活动的主流程。课程基础资料由 `CourseController`、`CourseService`、`CourseDao` 和 `Course` 实体承担，负责课程编号、课程名称、学分、学时、课程类型、考核方式和状态维护。任课任务由 `TeachingTaskController` 调用 `AcademicBusinessService` 和 `AcademicBusinessDao` 处理，负责把课程、教师、学期、节次、教室和容量组合成具体开课记录。学生选课与退课由 `StudentCourseController` 调用同一综合业务服务处理，负责可选课程查询、选课、退课和成绩更新。

| 类或职责 | 数据成员 | 函数名 | 返回值 | 参数 | 调用关系说明 |
| --- | --- | --- | --- | --- | --- |
| CourseController | courseService、operationLogService | list/get/create/update/delete | Result | 课程编号或课程表单 | 前端课程管理页面调用，服务层再访问课程 Dao |
| CourseService | courseDao | list/get/create/update/delete | 列表、实体或空结果 | 课程实体或课程编号 | 处理课程基础校验并调用 Dao |
| TeachingTaskController | service | list | Result 任课任务列表 | 可选教师编号 | 管理员和教师页面按条件查看任课任务 |
| TeachingTaskController | service | create/update/delete | Result 空结果 | 任课任务表单或编号 | 维护开课任务，关联课程和教师 |
| StudentCourseController | service、operationLogService | selectable | Result 可选课程列表 | 学生编号、学期 | 学生端查询可选择课程 |
| StudentCourseController | service、operationLogService | select/drop | Result 空结果 | 选课表单或选课记录编号 | 执行选课和退课，并记录日志 |

该组业务的调用关系具有明显顺序。首先管理员维护课程基础资料；其次管理员创建任课任务，指定教师、课程、学期、时间和容量；然后学生端基于任课任务进行选课；最后教师基于任课任务查看学生名单并录入成绩。`CourseService` 相对独立，适合处理课程基础信息；`TeachingTaskService` 和 `SelectionService` 是目录中的理想拆分名称，当前项目以 `AcademicBusinessService` 集中处理任课和选课业务。这样的实现可以减少课程设计阶段的类数量，但报告需要说明其职责仍然分明：任课管理解决课程如何开设，选课管理解决学生如何占用课程容量，二者通过 `teaching_task` 与 `student_course` 两张表衔接。

## 4.1.4 成绩录入与 GPA 折算业务类（GradeController / GradeService / GradeDao）

成绩录入与 GPA 折算业务在当前项目中未单独拆分为 `GradeController`、`GradeService`、`GradeDao` 三个文件，而是复用选课成绩表相关接口实现。实际控制入口是 `StudentCourseController` 的成绩更新职责，业务逻辑由 `AcademicBusinessService` 处理，数据访问由 `AcademicBusinessDao` 更新 `student_course` 表。该设计的依据是成绩依附于学生选课记录，成绩字段、成绩状态和备注均位于 `student_course` 表中，因此成绩模块与选课模块共享数据结构。报告中仍可按成绩模块说明职责，但应明确其当前实现映射。

| 类或职责 | 数据成员 | 函数名 | 返回值 | 参数 | 调用关系说明 |
| --- | --- | --- | --- | --- | --- |
| StudentCourseController | service、operationLogService | list | Result 选课成绩列表 | 学生编号、教师编号 | 学生查成绩或教师查课程学生名单 |
| StudentCourseController | service、operationLogService | updateScore | Result 空结果 | 选课记录编号、成绩表单、会话、请求对象 | 教师录入或修改成绩并记录操作日志 |
| 设计职责 GradeController | gradeService | updateGrade | Result 空结果 | 选课记录编号、成绩 | 后续可独立承担成绩录入接口 |
| AcademicBusinessService | dao | updateScore | 空结果 | 选课记录编号、分数 | 校验成绩范围并调用数据访问层 |
| AcademicBusinessDao | jdbcTemplate | updateStudentCourseScore | 更新行数 | 选课记录编号、分数 | 更新成绩和成绩状态 |
| 设计职责 GPA 计算器 | 折算规则 | calculateGpa | GPA 数值 | 成绩列表、学分 | 后续可封装百分制到绩点和学分加权计算 |

成绩模块的数据成员重点是选课记录编号、学生编号、任课任务编号、课程学分、分数、成绩状态和选课状态。录入成绩前必须确认记录存在，且分数在 0 到 100 范围内；查询 GPA 时应只统计已录入成绩，并按学分进行加权。当前项目学生端页面已经能够基于成绩数据计算平均分、已获学分等展示指标，GPA 折算可在此基础上作为规则扩展。测试该模块时，应准备 0 分、59.5 分、60 分、89.9 分、100 分、空成绩和越界成绩等数据，以证明边界处理正确。

## 4.1.5 报表统计与 ECharts 数据桥接类（ReportController / ReportService / ChartDataVO）

报表统计模块的当前后端入口是 `ReportController`，其数据成员主要为综合业务服务对象。控制器提供概览、学院学生人数、成绩分布和教师任课负载等接口。服务职责由 `AcademicBusinessService` 承担，数据访问职责由 `AcademicBusinessDao` 承担，前端通过 `report.js` 调用接口，并在管理员报表页面或仪表盘页面使用 ECharts 展示。目录中的 `ReportService` 和 `ChartDataVO` 可以视为后续增强设计：前者用于独立封装统计业务，后者用于定义图表数据的标准结构，例如分类轴、指标值、系列名称和附加说明。

| 类或职责 | 数据成员 | 函数名 | 返回值 | 参数 | 调用关系说明 |
| --- | --- | --- | --- | --- | --- |
| ReportController | service | overview | Result 概览指标 | 无 | 返回学生数、教师数、课程数等汇总指标 |
| ReportController | service | collegeStudents | Result 学院学生统计 | 无 | 返回学院维度学生人数，供图表展示 |
| ReportController | service | gradeDistribution | Result 成绩分布 | 无 | 返回成绩区间统计，供柱状或饼图展示 |
| ReportController | service | teachingLoad | Result 教师任课负载 | 无 | 返回教师维度任课数量或相关指标 |
| 设计职责 ReportService | reportDao | buildReportData | 图表数据 | 报表类型、筛选条件 | 后续可独立封装报表口径 |
| 设计职责 ChartDataVO | categories、series、values | 无业务函数 | 数据对象 | 无 | 后续可统一前端图表数据结构 |

该模块调用链较短，但对数据口径要求较高。前端不应自行拼接复杂统计逻辑，而应从后端获取已经按业务规则聚合的数据。后端统计查询应排除停用或无效记录，成绩分布应只统计已录入成绩，教师负载应基于任课任务而不是单纯教师数量。当前项目使用映射结果直接返回，具有实现简单、适合 ECharts 消费的优点；不足是缺少强类型图表数据对象，字段命名需要前后端约定一致。后续若报表种类增加，可以引入 `ChartDataVO` 统一图表协议，降低页面对具体 SQL 别名的依赖。

## 4.1.6 统一返回、分页与异常处理类（Result / PageResult / BusinessException / GlobalExceptionHandler）

统一返回、分页与异常处理类为系统接口提供通用规范。`Result` 用于表达普通接口响应，数据成员包括状态码、提示信息和业务数据；`PageResult` 用于表达分页查询结果，数据成员包括记录列表、总数、当前页和每页大小；`BusinessException` 用于表达业务规则不满足的错误；`GlobalExceptionHandler` 用于捕获业务异常、参数异常、数据访问异常和运行时异常，并转换为统一响应。该组类不直接属于某个业务模块，但所有控制器都会间接受益于它们。

| 类或职责 | 数据成员 | 函数名 | 返回值 | 参数 | 调用关系说明 |
| --- | --- | --- | --- | --- | --- |
| Result | code、message、data | success | Result | 可选数据 | 控制器成功时统一返回 |
| Result | code、message、data | error、badRequest、unauthorized、forbidden、notFound、serverError | Result | 提示信息 | 异常处理或业务失败时统一返回 |
| PageResult | records、total、page、size | 构造与访问函数 | 分页对象 | 记录列表和分页参数 | 日志分页等接口使用 |
| BusinessException | message | 构造函数 | 异常对象 | 错误提示 | Service 层发现业务错误时使用 |
| GlobalExceptionHandler | 日志对象 | handleBusinessException 等 | Result 空结果 | 异常对象、请求对象 | 捕获不同异常并返回统一错误 |
| OperationLogService | operationLogDao | record、page | 空结果或分页结果 | 请求、会话、模块、操作类型等 | 记录操作并支持日志查询 |

调用关系上，普通业务接口由 Controller 返回 `Result`；分页查询接口可以把 `PageResult` 放入 `Result` 数据中返回；业务层发现重复数据、容量超限、成绩越界等问题时交给业务异常机制；全局异常处理类统一兜底，避免异常格式分散。前端 Axios 可以基于统一结构判断请求是否成功，并通过消息组件显示错误。该设计降低了前后端联调成本，也使测试用例更容易编写，因为每个异常场景都可以检查状态码、提示信息和数据是否为空，而不需要关注后端内部异常细节。
