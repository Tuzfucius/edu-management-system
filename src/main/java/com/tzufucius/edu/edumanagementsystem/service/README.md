# service

该目录存放业务服务，负责业务校验、事务边界和 DAO 编排。

公开方法以 request/vo 为主要边界类型，不向 controller 暴露 entity 或 `Map<String, Object>`。
