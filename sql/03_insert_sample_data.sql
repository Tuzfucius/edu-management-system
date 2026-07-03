-- 教务管理系统模拟数据脚本
-- 执行顺序：第 3 个执行
-- 前置条件：已执行 01_create_database.sql 和 02_create_tables.sql

USE edu_management_system;

INSERT INTO sys_user (id, username, password, role, status) VALUES
(1, 'admin', '123456', 'ADMIN', 1),
(2, 't1001', '123456', 'TEACHER', 1),
(3, 't1002', '123456', 'TEACHER', 1),
(4, 's20240001', '123456', 'STUDENT', 1),
(5, 's20240002', '123456', 'STUDENT', 1),
(6, 's20240003', '123456', 'STUDENT', 1);

INSERT INTO college (id, college_code, college_name, description, status) VALUES
(1, 'CS', '计算机学院', '负责计算机类专业教学与科研', 1),
(2, 'MATH', '数学学院', '负责数学基础课程与应用数学专业建设', 1);

INSERT INTO major (id, college_id, major_code, major_name, schooling_years, degree_type, status) VALUES
(1, 1, 'CSSE', '软件工程', 4, '工学', 1),
(2, 1, 'CSAI', '人工智能', 4, '工学', 1),
(3, 2, 'MATHAPP', '应用数学', 4, '理学', 1);

INSERT INTO department (id, college_id, department_code, department_name, office_location, status) VALUES
(1, 1, 'D-CS-SW', '软件工程教研室', '信息楼 A301', 1),
(2, 1, 'D-CS-AI', '人工智能教研室', '信息楼 A402', 1),
(3, 2, 'D-MATH-BASIC', '公共数学教研室', '理学楼 B201', 1);

INSERT INTO class_info (id, major_id, class_code, class_name, entrance_year, status) VALUES
(1, 1, 'SE2024-1', '软件工程2024级1班', 2024, 1),
(2, 2, 'AI2024-1', '人工智能2024级1班', 2024, 1),
(3, 3, 'AM2024-1', '应用数学2024级1班', 2024, 1);

INSERT INTO teacher (
    id, user_id, department_id, teacher_no, teacher_name, gender, title, phone, email, status
) VALUES
(1, 2, 1, 'T1001', '张明', 'M', '副教授', '13800000001', 'zhangming@example.edu', 1),
(2, 3, 2, 'T1002', '李华', 'F', '讲师', '13800000002', 'lihua@example.edu', 1);

INSERT INTO student (
    id, user_id, class_id, student_no, student_name, gender, birth_date, phone, email, enrollment_year, status
) VALUES
(1, 4, 1, '20240001', '王小明', 'M', '2006-03-12', '13900000001', '20240001@example.edu', 2024, 1),
(2, 5, 1, '20240002', '赵雨', 'F', '2006-07-21', '13900000002', '20240002@example.edu', 2024, 1),
(3, 6, 2, '20240003', '陈晨', 'M', '2006-01-18', '13900000003', '20240003@example.edu', 2024, 1);

INSERT INTO course (
    id, course_code, course_name, credit, total_hours, course_type, exam_type, status
) VALUES
(1, 'CS101', 'Java程序设计', 3.0, 48, '必修', '考试', 1),
(2, 'CS102', '数据库原理', 3.0, 48, '必修', '考试', 1),
(3, 'AI101', '人工智能导论', 2.0, 32, '选修', '考查', 1);

INSERT INTO teaching_task (
    id, course_id, teacher_id, semester, weekday, start_section, end_section, weeks, classroom, capacity, selected_count, task_status
) VALUES
(1, 1, 1, '2025-2026-1', 1, 1, 2, '1-16周', '信息楼 A101', 60, 2, 1),
(2, 2, 1, '2025-2026-1', 3, 3, 4, '1-16周', '信息楼 A102', 60, 2, 1),
(3, 3, 2, '2025-2026-1', 5, 5, 6, '1-12周', '信息楼 A201', 50, 1, 1);

INSERT INTO student_course (
    id, student_id, teaching_task_id, score, grade_status, status, remark
) VALUES
(1, 1, 1, 86.50, 1, 1, '正常完成课程'),
(2, 2, 1, 92.00, 1, 1, '正常完成课程'),
(3, 1, 2, NULL, 0, 1, NULL),
(4, 3, 2, NULL, 0, 1, NULL),
(5, 3, 3, 88.00, 1, 1, '正常完成课程');

INSERT INTO teacher_student (
    id, teacher_id, student_id, guide_type, start_date, end_date, status
) VALUES
(1, 1, 1, '课程设计指导', '2026-07-01', NULL, 1),
(2, 2, 3, '学业导师', '2026-07-01', NULL, 1);

INSERT INTO operation_log (
    user_id, module_name, operation_type, target_table, target_id, description, ip_address
) VALUES
(1, '系统初始化', 'INSERT', 'sys_user', 1, '导入基础模拟数据', '127.0.0.1');
