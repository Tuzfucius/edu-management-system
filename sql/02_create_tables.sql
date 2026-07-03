-- 教务管理系统建表脚本
-- 执行顺序：第 2 个执行
-- 前置条件：已执行 01_create_database.sql

USE edu_management_system;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS operation_log;
DROP TABLE IF EXISTS teacher_student;
DROP TABLE IF EXISTS student_course;
DROP TABLE IF EXISTS teaching_task;
DROP TABLE IF EXISTS course;
DROP TABLE IF EXISTS teacher;
DROP TABLE IF EXISTS student;
DROP TABLE IF EXISTS class_info;
DROP TABLE IF EXISTS department;
DROP TABLE IF EXISTS major;
DROP TABLE IF EXISTS college;
DROP TABLE IF EXISTS sys_user;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL COMMENT '登录账号',
    password VARCHAR(100) NOT NULL COMMENT '登录密码，课程设计初期可使用明文，后续可改为加密',
    role VARCHAR(20) NOT NULL COMMENT '角色：ADMIN/TEACHER/STUDENT',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '账号状态：1正常，0停用',
    last_login_at DATETIME NULL COMMENT '最后登录时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT uk_sys_user_username UNIQUE (username),
    CONSTRAINT ck_sys_user_role CHECK (role IN ('ADMIN', 'TEACHER', 'STUDENT')),
    CONSTRAINT ck_sys_user_status CHECK (status IN (0, 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户表';

CREATE TABLE college (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '学院ID',
    college_code VARCHAR(30) NOT NULL COMMENT '学院编号',
    college_name VARCHAR(100) NOT NULL COMMENT '学院名称',
    description VARCHAR(255) NULL COMMENT '学院简介',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1正常，0停用',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT uk_college_code UNIQUE (college_code),
    CONSTRAINT ck_college_status CHECK (status IN (0, 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学院表';

CREATE TABLE major (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '专业ID',
    college_id BIGINT NOT NULL COMMENT '所属学院ID',
    major_code VARCHAR(30) NOT NULL COMMENT '专业编号',
    major_name VARCHAR(100) NOT NULL COMMENT '专业名称',
    schooling_years TINYINT NOT NULL DEFAULT 4 COMMENT '学制',
    degree_type VARCHAR(50) NULL COMMENT '学位类型',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1正常，0停用',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT uk_major_code UNIQUE (major_code),
    CONSTRAINT fk_major_college FOREIGN KEY (college_id) REFERENCES college (id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT ck_major_status CHECK (status IN (0, 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='专业表';

CREATE TABLE department (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '教研室ID',
    college_id BIGINT NOT NULL COMMENT '所属学院ID',
    department_code VARCHAR(30) NOT NULL COMMENT '教研室编号',
    department_name VARCHAR(100) NOT NULL COMMENT '教研室名称',
    office_location VARCHAR(100) NULL COMMENT '办公地点',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1正常，0停用',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT uk_department_code UNIQUE (department_code),
    CONSTRAINT fk_department_college FOREIGN KEY (college_id) REFERENCES college (id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT ck_department_status CHECK (status IN (0, 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教研室表';

CREATE TABLE class_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '班级ID',
    major_id BIGINT NOT NULL COMMENT '所属专业ID',
    class_code VARCHAR(30) NOT NULL COMMENT '班级编号',
    class_name VARCHAR(100) NOT NULL COMMENT '班级名称',
    entrance_year SMALLINT NOT NULL COMMENT '入学年份',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1正常，0停用',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT uk_class_info_code UNIQUE (class_code),
    CONSTRAINT fk_class_info_major FOREIGN KEY (major_id) REFERENCES major (id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT ck_class_info_status CHECK (status IN (0, 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='班级表';

CREATE TABLE student (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '学生ID',
    user_id BIGINT NOT NULL COMMENT '对应用户ID',
    class_id BIGINT NOT NULL COMMENT '所属班级ID',
    student_no VARCHAR(30) NOT NULL COMMENT '学号',
    student_name VARCHAR(50) NOT NULL COMMENT '学生姓名',
    gender CHAR(1) NULL COMMENT '性别：M/F',
    birth_date DATE NULL COMMENT '出生日期',
    phone VARCHAR(20) NULL COMMENT '手机号',
    email VARCHAR(100) NULL COMMENT '邮箱',
    enrollment_year SMALLINT NOT NULL COMMENT '入学年份',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '学籍状态：1正常，0停用',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT uk_student_user UNIQUE (user_id),
    CONSTRAINT uk_student_no UNIQUE (student_no),
    CONSTRAINT fk_student_user FOREIGN KEY (user_id) REFERENCES sys_user (id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_student_class FOREIGN KEY (class_id) REFERENCES class_info (id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT ck_student_gender CHECK (gender IS NULL OR gender IN ('M', 'F')),
    CONSTRAINT ck_student_status CHECK (status IN (0, 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生表';

CREATE TABLE teacher (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '教师ID',
    user_id BIGINT NOT NULL COMMENT '对应用户ID',
    department_id BIGINT NOT NULL COMMENT '所属教研室ID',
    teacher_no VARCHAR(30) NOT NULL COMMENT '教师工号',
    teacher_name VARCHAR(50) NOT NULL COMMENT '教师姓名',
    gender CHAR(1) NULL COMMENT '性别：M/F',
    title VARCHAR(50) NULL COMMENT '职称',
    phone VARCHAR(20) NULL COMMENT '手机号',
    email VARCHAR(100) NULL COMMENT '邮箱',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1正常，0停用',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT uk_teacher_user UNIQUE (user_id),
    CONSTRAINT uk_teacher_no UNIQUE (teacher_no),
    CONSTRAINT fk_teacher_user FOREIGN KEY (user_id) REFERENCES sys_user (id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_teacher_department FOREIGN KEY (department_id) REFERENCES department (id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT ck_teacher_gender CHECK (gender IS NULL OR gender IN ('M', 'F')),
    CONSTRAINT ck_teacher_status CHECK (status IN (0, 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教师表';

CREATE TABLE course (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '课程ID',
    course_code VARCHAR(30) NOT NULL COMMENT '课程编号',
    course_name VARCHAR(100) NOT NULL COMMENT '课程名称',
    credit DECIMAL(3,1) NOT NULL COMMENT '学分',
    total_hours INT NULL COMMENT '总学时',
    course_type VARCHAR(20) NULL COMMENT '课程类型：必修/选修',
    exam_type VARCHAR(20) NULL COMMENT '考核类型：考试/考查',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1正常，0停用',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT uk_course_code UNIQUE (course_code),
    CONSTRAINT ck_course_credit CHECK (credit > 0),
    CONSTRAINT ck_course_status CHECK (status IN (0, 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程表';

CREATE TABLE teaching_task (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '任课记录ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    teacher_id BIGINT NOT NULL COMMENT '教师ID',
    semester VARCHAR(20) NOT NULL COMMENT '学期，例如 2025-2026-1',
    weekday TINYINT NULL COMMENT '星期几：1-7',
    start_section TINYINT NULL COMMENT '开始节次',
    end_section TINYINT NULL COMMENT '结束节次',
    weeks VARCHAR(50) NULL COMMENT '上课周次',
    classroom VARCHAR(100) NULL COMMENT '教室',
    capacity INT NOT NULL DEFAULT 60 COMMENT '课程容量',
    selected_count INT NOT NULL DEFAULT 0 COMMENT '已选人数',
    task_status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1可选，2已结课，0停用',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT fk_teaching_task_course FOREIGN KEY (course_id) REFERENCES course (id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_teaching_task_teacher FOREIGN KEY (teacher_id) REFERENCES teacher (id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT ck_teaching_task_weekday CHECK (weekday IS NULL OR weekday BETWEEN 1 AND 7),
    CONSTRAINT ck_teaching_task_section CHECK (
        start_section IS NULL OR end_section IS NULL OR start_section <= end_section
    ),
    CONSTRAINT ck_teaching_task_capacity CHECK (capacity >= 0),
    CONSTRAINT ck_teaching_task_selected CHECK (selected_count >= 0 AND selected_count <= capacity),
    CONSTRAINT ck_teaching_task_status CHECK (task_status IN (0, 1, 2))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任课安排表';

CREATE TABLE student_course (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '选课记录ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    teaching_task_id BIGINT NOT NULL COMMENT '任课记录ID',
    select_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '选课时间',
    score DECIMAL(5,2) NULL COMMENT '成绩',
    grade_status TINYINT NOT NULL DEFAULT 0 COMMENT '成绩状态：0未录入，1已录入',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '选课状态：1已选，0已退课',
    remark VARCHAR(255) NULL COMMENT '备注',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT uk_student_course_task UNIQUE (student_id, teaching_task_id),
    CONSTRAINT fk_student_course_student FOREIGN KEY (student_id) REFERENCES student (id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_student_course_teaching_task FOREIGN KEY (teaching_task_id) REFERENCES teaching_task (id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT ck_student_course_score CHECK (score IS NULL OR (score >= 0 AND score <= 100)),
    CONSTRAINT ck_student_course_grade_status CHECK (grade_status IN (0, 1)),
    CONSTRAINT ck_student_course_status CHECK (status IN (0, 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生选课与成绩表';

CREATE TABLE teacher_student (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '指导关系ID',
    teacher_id BIGINT NOT NULL COMMENT '教师ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    guide_type VARCHAR(50) NULL COMMENT '指导类型',
    start_date DATE NULL COMMENT '开始日期',
    end_date DATE NULL COMMENT '结束日期',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1正常，0停用',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT uk_teacher_student UNIQUE (teacher_id, student_id),
    CONSTRAINT fk_teacher_student_teacher FOREIGN KEY (teacher_id) REFERENCES teacher (id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_teacher_student_student FOREIGN KEY (student_id) REFERENCES student (id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT ck_teacher_student_date CHECK (end_date IS NULL OR start_date IS NULL OR start_date <= end_date),
    CONSTRAINT ck_teacher_student_status CHECK (status IN (0, 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教师指导学生表';

CREATE TABLE operation_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    user_id BIGINT NULL COMMENT '操作用户ID',
    module_name VARCHAR(50) NOT NULL COMMENT '模块名称',
    operation_type VARCHAR(30) NOT NULL COMMENT '操作类型',
    target_table VARCHAR(50) NULL COMMENT '被操作表',
    target_id BIGINT NULL COMMENT '被操作记录ID',
    description VARCHAR(255) NULL COMMENT '操作描述',
    ip_address VARCHAR(50) NULL COMMENT 'IP地址',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    CONSTRAINT fk_operation_log_user FOREIGN KEY (user_id) REFERENCES sys_user (id)
        ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

CREATE INDEX idx_major_college_id ON major (college_id);
CREATE INDEX idx_department_college_id ON department (college_id);
CREATE INDEX idx_class_info_major_id ON class_info (major_id);
CREATE INDEX idx_student_class_id ON student (class_id);
CREATE INDEX idx_teacher_department_id ON teacher (department_id);
CREATE INDEX idx_teaching_task_course_id ON teaching_task (course_id);
CREATE INDEX idx_teaching_task_teacher_id ON teaching_task (teacher_id);
CREATE INDEX idx_teaching_task_semester ON teaching_task (semester);
CREATE INDEX idx_student_course_student_id ON student_course (student_id);
CREATE INDEX idx_student_course_task_id ON student_course (teaching_task_id);
CREATE INDEX idx_teacher_student_teacher_id ON teacher_student (teacher_id);
CREATE INDEX idx_teacher_student_student_id ON teacher_student (student_id);
CREATE INDEX idx_operation_log_user_id ON operation_log (user_id);
CREATE INDEX idx_operation_log_created_at ON operation_log (created_at);
