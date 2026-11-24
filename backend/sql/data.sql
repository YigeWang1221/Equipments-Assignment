USE equip_management;

-- 演示账号密码统一为 123456
SET @pwd = '$2y$10$Tx9r0jBBjewg0Ujlss3qGuLUg4PItqwyEPZ0F3qNRGe7S02kawU.i';

INSERT INTO teacher (id, username, password, name, phone, role, status) VALUES
  (1, 'teacher01', @pwd, '傅均老师', '13900000001', 'TEACHER', 1),
  (2, 'teacher02', @pwd, '李老师', '13900000002', 'TEACHER', 1);

INSERT INTO student (stu_id, stu_name, phone, password, college, admin_class_name, status) VALUES
  (2023000001, '王亦格', '13800000001', @pwd, '信息与电子工程学院', '物联网2301', 1),
  (2023000002, '陈一鸣', '13800000002', @pwd, '信息与电子工程学院', '物联网2301', 1),
  (2023000003, '林小满', '13800000003', @pwd, '信息与电子工程学院', '物联网2301', 1),
  (2023000004, '赵子涵', '13800000004', @pwd, '信息与电子工程学院', '物联网2301', 1),
  (2023000005, '周予安', '13800000005', @pwd, '信息与电子工程学院', '嵌入式2301', 1),
  (2023000006, '吴思远', '13800000006', @pwd, '信息与电子工程学院', '嵌入式2301', 1),
  (2023000007, '郑雨桐', '13800000007', @pwd, '信息与电子工程学院', '嵌入式2301', 1),
  (2023000008, '孙浩然', '13800000008', @pwd, '信息与电子工程学院', '嵌入式2301', 1);

INSERT INTO class_info (id, class_name, class_type, year, start_time, end_time, teacher_id, status) VALUES
  (1, '2024物联网课程1班', 2, 2024, '2024-09-01', '2025-01-15', 1, 1),
  (2, '2024嵌入式课程1班', 1, 2024, '2024-09-01', '2025-01-15', 1, 1),
  (3, '2023物联网课程1班', 2, 2023, '2023-09-01', '2024-01-15', 2, 1);

INSERT INTO class_group (id, class_id, group_name, group_num, teaching_class_name, members_text) VALUES
  (1, 1, '第1组', 2, '物联网2301', '2023000001 王亦格, 2023000002 陈一鸣'),
  (2, 1, '第2组', 2, '物联网2301', '2023000003 林小满, 2023000004 赵子涵'),
  (3, 2, '第1组', 2, '嵌入式2301', '2023000005 周予安, 2023000006 吴思远'),
  (4, 2, '第2组', 2, '嵌入式2301', '2023000007 郑雨桐, 2023000008 孙浩然'),
  (5, 3, '第1组', 2, '物联网2201', '2022000001 示例同学, 2022000002 示例同学');

INSERT INTO student_class (stu_id, class_id, teaching_class_name, group_id, joined_at) VALUES
  (2023000001, 1, '物联网2301', 1, '2024-09-01 08:00:00'),
  (2023000002, 1, '物联网2301', 1, '2024-09-01 08:00:00'),
  (2023000003, 1, '物联网2301', 2, '2024-09-01 08:00:00'),
  (2023000004, 1, '物联网2301', 2, '2024-09-01 08:00:00'),
  (2023000005, 2, '嵌入式2301', 3, '2024-09-01 08:00:00'),
  (2023000006, 2, '嵌入式2301', 3, '2024-09-01 08:00:00'),
  (2023000007, 2, '嵌入式2301', 4, '2024-09-01 08:00:00'),
  (2023000008, 2, '嵌入式2301', 4, '2024-09-01 08:00:00');

INSERT INTO teacher_work (id, class_id, work_name, content_html, content_json, detail_url, start_time, end_time, status, created_by) VALUES
  (1, 1, 'STM32 环境搭建与点灯实验',
   '<h2>实验目标</h2><p>完成 Keil 工程创建，使用 GPIO 控制 LED 闪烁。</p><h2>提交要求</h2><p>提交实验报告与演示视频。</p>',
   '{"blocks":[{"type":"heading","text":"实验目标"},{"type":"paragraph","text":"完成 Keil 工程创建，使用 GPIO 控制 LED 闪烁。"}]}',
   NULL, '2024-09-10 08:00:00', '2099-12-31 23:59:59', 1, 1),
  (2, 1, 'MQTT 温湿度数据上报',
   '<h2>实验目标</h2><p>使用 ESP8266 采集温湿度并通过 MQTT 上报到平台。</p>',
   '{"blocks":[{"type":"heading","text":"实验目标"},{"type":"paragraph","text":"采集温湿度并通过 MQTT 上报。"}]}',
   NULL, '2024-09-01 08:00:00', '2024-10-01 23:59:59', 2, 1),
  (3, 2, 'ARM 汇编排序实验',
   '<h2>实验目标</h2><p>使用 ARM 汇编实现冒泡排序并验证结果。</p>',
   '{"blocks":[{"type":"heading","text":"实验目标"},{"type":"paragraph","text":"ARM 汇编冒泡排序。"}]}',
   NULL, '2024-09-01 08:00:00', '2024-10-01 23:59:59', 2, 1),
  (4, 3, '传感器数据采集综合实验',
   '<h2>实验目标</h2><p>完成多传感器数据采集与可视化展示。</p>',
   '{"blocks":[{"type":"heading","text":"实验目标"},{"type":"paragraph","text":"多传感器数据采集。"}]}',
   NULL, '2023-10-01 08:00:00', '2023-12-31 23:59:59', 2, 2);

INSERT INTO stu_work (id, teacher_work_id, group_id, work_doc, video_url, zip_url, score, submit_status, submitted_at) VALUES
  (1, 2, 1, '<h2>实验过程</h2><p>完成 ESP8266 与 DHT11 接线，成功上报温湿度数据。</p>', NULL, NULL, 92.00, 3, '2024-09-28 20:00:00'),
  (2, 3, 3, '<h2>实验过程</h2><p>使用 ARM 汇编完成冒泡排序，测试通过。</p>', NULL, NULL, 88.50, 3, '2024-09-29 19:00:00'),
  (3, 4, 5, '<h2>实验过程</h2><p>综合实验报告。</p>', NULL, NULL, 95.00, 3, '2023-12-20 18:00:00');

INSERT INTO great_work (id, stu_work_id, work_name, photo_url, class_type, year) VALUES
  (1, 1, 'MQTT 温湿度监测系统', NULL, 2, 2024),
  (2, 2, 'ARM 汇编排序演示', NULL, 1, 2024);

INSERT INTO equipment (id, name, photo_url, info_url, total_number, used_number, status) VALUES
  (1, 'STM32F103 开发板', NULL, NULL, 20, 2, 1),
  (2, 'ESP8266 物联网开发套件', NULL, NULL, 15, 1, 1),
  (3, '数字示波器', NULL, NULL, 5, 0, 1),
  (4, 'DHT11 温湿度传感器', NULL, NULL, 40, 6, 1);

INSERT INTO equipment_borrow (id, equipment_id, stu_id, stu_name, num, status, apply_time) VALUES
  (1, 1, 2023000001, '王亦格', 1, 0, '2024-10-08 10:00:00'),
  (2, 2, 2023000005, '周予安', 1, 1, '2024-10-07 09:00:00'),
  (3, 4, 2023000003, '林小满', 2, 2, '2024-10-01 09:00:00');
