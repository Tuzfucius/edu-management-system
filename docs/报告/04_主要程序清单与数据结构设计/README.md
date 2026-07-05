# 第 4 章 主要程序清单与数据结构设计

本章从程序接口和数据结构两个方面说明系统实现。程序清单不展示任何源代码，只列模块职责、主要数据成员、函数名、返回值、参数和调用关系。数据结构设计以数据库逻辑模型和物理表结构为核心，说明 12 张业务表的字段类型、主键、外键、唯一约束、状态字段和事务处理原则。

当前项目中部分业务类与目录中的理想类名存在差异。例如成绩模块没有单独拆分 `GradeController`、`GradeService`、`GradeDao`，而是由 `StudentCourseController`、`AcademicBusinessService`、`AcademicBusinessDao` 协同承担；报表数据没有单独 `ChartDataVO`，而是以映射结果返回给前端 ECharts 页面。报告在尊重课程目录的同时，按项目真实结构说明这些职责映射。
