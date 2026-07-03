# SQL 脚本说明

本目录用于手动管理教务管理系统的 MySQL 数据库脚本。当前采用基础方式，不引入 Flyway 或 Spring Boot 自动初始化。

## 执行顺序

1. `01_create_database.sql`：创建数据库并切换到 `edu_management_system`。
2. `02_create_tables.sql`：按外键依赖顺序创建 12 张表。
3. `03_insert_sample_data.sql`：插入用于开发、演示和答辩的模拟数据。

## 执行方式

在 MySQL 命令行中执行：

```bash
mysql -u root -p < sql/01_create_database.sql
mysql -u root -p < sql/02_create_tables.sql
mysql -u root -p < sql/03_insert_sample_data.sql
```

本机 Windows 11 环境中，MySQL 客户端绝对路径为：

```text
C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe
```

PowerShell 中建议通过 `source` 执行脚本：

```powershell
& "C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe" -u root -p -e "source E:/Project/edu-management-system/sql/01_create_database.sql"
& "C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe" -u root -p -e "source E:/Project/edu-management-system/sql/02_create_tables.sql"
& "C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe" -u root -p -e "source E:/Project/edu-management-system/sql/03_insert_sample_data.sql"
```

也可以在 Navicat、DataGrip、MySQL Workbench 中按文件顺序手动执行。

## 维护规则

- 表结构以 `docs/数据库计划.md` 为主要依据；如果与 `docs/整体计划.md` 存在出入，以具体数据库计划为准。
- 业务表统一使用 `id BIGINT PRIMARY KEY AUTO_INCREMENT` 作为数据库主键，业务编号使用唯一约束。
- 主要业务数据默认采用 `status` 字段表示启用、停用或退课状态，避免直接删除破坏历史记录。
- 后续如果需要调整表结构，优先新增 `04_xxx.sql` 这类增量脚本，不直接覆盖已用于演示或部署的历史脚本。
