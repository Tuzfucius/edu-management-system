# dao

该目录存放数据访问组件，负责通过 JDBC 访问数据库并完成结果集到实体或 VO 的映射。

DAO 不向 service 暴露 `Map<String, Object>` 作为业务数据载体。
