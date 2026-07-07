# 认证与鉴权

本目录提供基于 Session 的后端权限控制。

- `AuthInterceptor` 统一拦截 `/api/**` 请求，除登录接口外都要求 Session 中存在 `loginUser`。
- `RequireRole` 用于在 Controller 类或方法上声明允许访问的角色。
- `AuthContext` 封装当前用户读取、角色判断和业务层资源归属校验所需的基础方法。
