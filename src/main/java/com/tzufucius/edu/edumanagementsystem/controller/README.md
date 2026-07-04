# controller 目录说明

本目录保存 REST 控制器，负责接收前端请求、调用 Service 层并统一返回 `Result`。

Controller 不直接拼接 SQL，也不写复杂业务校验；业务规则应放在 Service 层。
