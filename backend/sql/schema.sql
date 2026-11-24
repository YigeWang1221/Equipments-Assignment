-- Equipment borrowing and course assignment management system
-- MySQL 8.x schema

CREATE DATABASE IF NOT EXISTS equip_management
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE equip_management;

DROP TABLE IF EXISTS login_log;
DROP TABLE IF EXISTS lab_usage_log;
DROP TABLE IF EXISTS file_object;
DROP TABLE IF EXISTS great_work;
DROP TABLE IF EXISTS stu_work;
DROP TABLE IF EXISTS teacher_work;
DROP TABLE IF EXISTS equipment_borrow;
DROP TABLE IF EXISTS equipment;
DROP TABLE IF EXISTS student_class;
DROP TABLE IF EXISTS class_group;
DROP TABLE IF EXISTS class_info;
DROP TABLE IF EXISTS student;
DROP TABLE IF EXISTS teacher;
DROP TABLE IF EXISTS guest;

CREATE TABLE teacher (
  id            BIGINT       NOT NULL AUTO_INCREMENT,
  username      VARCHAR(50)  NOT NULL,
  password      VARCHAR(100) NOT NULL,
  name          VARCHAR(50)  NOT NULL,
  phone         VARCHAR(20)  DEFAULT NULL,
  role          VARCHAR(20)  NOT NULL DEFAULT 'TEACHER',
  status        TINYINT      NOT NULL DEFAULT 1,
  created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_teacher_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师/管理员';

CREATE TABLE student (
  stu_id          BIGINT       NOT NULL COMMENT '10位学号',
  stu_name        VARCHAR(50)  NOT NULL,
  phone           VARCHAR(20)  DEFAULT NULL,
  password        VARCHAR(100) NOT NULL,
  college         VARCHAR(100) DEFAULT NULL,
  admin_class_name VARCHAR(100) DEFAULT NULL,
  status          TINYINT      NOT NULL DEFAULT 1,
  created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (stu_id),
  KEY idx_student_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生';

CREATE TABLE guest (
  id         BIGINT      NOT NULL AUTO_INCREMENT,
  phone      VARCHAR(20) NOT NULL,
  name       VARCHAR(50) DEFAULT NULL,
  created_at DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_guest_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='访客';

CREATE TABLE class_info (
  id          BIGINT       NOT NULL AUTO_INCREMENT,
  class_name  VARCHAR(100) NOT NULL,
  class_type  TINYINT      NOT NULL COMMENT '1=ARM嵌入式 2=IOT物联网',
  year        INT          NOT NULL,
  start_time  DATE         DEFAULT NULL,
  end_time    DATE         DEFAULT NULL,
  teacher_id  BIGINT       DEFAULT NULL,
  status      TINYINT      NOT NULL DEFAULT 1,
  created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_class_name_year (class_name, year),
  KEY idx_class_teacher (teacher_id),
  KEY idx_class_type (class_type),
  CONSTRAINT fk_class_teacher FOREIGN KEY (teacher_id) REFERENCES teacher (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教学班级';

CREATE TABLE class_group (
  id                  BIGINT       NOT NULL AUTO_INCREMENT,
  class_id            BIGINT       NOT NULL,
  group_name          VARCHAR(50)  NOT NULL,
  group_num           INT          NOT NULL DEFAULT 0,
  teaching_class_name VARCHAR(100) DEFAULT NULL,
  members_text        VARCHAR(2000) DEFAULT NULL,
  created_at          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_group_class_name (class_id, group_name),
  KEY idx_group_class (class_id),
  CONSTRAINT fk_group_class FOREIGN KEY (class_id) REFERENCES class_info (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级小组';

CREATE TABLE student_class (
  id                  BIGINT       NOT NULL AUTO_INCREMENT,
  stu_id              BIGINT       NOT NULL,
  class_id            BIGINT       NOT NULL,
  teaching_class_name VARCHAR(100) DEFAULT NULL,
  group_id            BIGINT       DEFAULT NULL,
  joined_at           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_student_class (stu_id, class_id),
  KEY idx_sc_class (class_id),
  KEY idx_sc_group (group_id),
  CONSTRAINT fk_sc_student FOREIGN KEY (stu_id) REFERENCES student (stu_id) ON DELETE CASCADE,
  CONSTRAINT fk_sc_class FOREIGN KEY (class_id) REFERENCES class_info (id) ON DELETE CASCADE,
  CONSTRAINT fk_sc_group FOREIGN KEY (group_id) REFERENCES class_group (id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生-班级-小组关系';

CREATE TABLE teacher_work (
  id            BIGINT       NOT NULL AUTO_INCREMENT,
  class_id      BIGINT       NOT NULL,
  work_name     VARCHAR(200) NOT NULL,
  content_html  LONGTEXT     DEFAULT NULL,
  content_json  LONGTEXT     DEFAULT NULL,
  detail_url    VARCHAR(500) DEFAULT NULL,
  start_time    DATETIME     DEFAULT NULL,
  end_time      DATETIME     DEFAULT NULL,
  status        TINYINT      NOT NULL DEFAULT 0 COMMENT '0未开始 1进行中 2截止',
  created_by    BIGINT       DEFAULT NULL,
  version       INT          NOT NULL DEFAULT 0,
  created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_work_class (class_id),
  KEY idx_work_time (start_time, end_time),
  CONSTRAINT fk_work_class FOREIGN KEY (class_id) REFERENCES class_info (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师作业';

CREATE TABLE stu_work (
  id              BIGINT       NOT NULL AUTO_INCREMENT,
  teacher_work_id BIGINT       NOT NULL,
  group_id        BIGINT       NOT NULL,
  work_doc        LONGTEXT     DEFAULT NULL,
  video_url       VARCHAR(500) DEFAULT NULL,
  zip_url         VARCHAR(500) DEFAULT NULL,
  score           DECIMAL(5,2) DEFAULT NULL,
  submit_status   TINYINT      NOT NULL DEFAULT 0 COMMENT '0未开始 1草稿 2已提交 3已批改',
  submitted_at    DATETIME     DEFAULT NULL,
  version         INT          NOT NULL DEFAULT 0,
  created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_stu_work (teacher_work_id, group_id),
  KEY idx_stu_work_group (group_id),
  CONSTRAINT fk_sw_work FOREIGN KEY (teacher_work_id) REFERENCES teacher_work (id) ON DELETE CASCADE,
  CONSTRAINT fk_sw_group FOREIGN KEY (group_id) REFERENCES class_group (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='小组作业提交';

CREATE TABLE great_work (
  id          BIGINT       NOT NULL AUTO_INCREMENT,
  stu_work_id BIGINT       NOT NULL,
  work_name   VARCHAR(200) NOT NULL,
  photo_url   VARCHAR(500) DEFAULT NULL,
  class_type  TINYINT      NOT NULL DEFAULT 2,
  year        INT          NOT NULL,
  created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_great_stu_work (stu_work_id),
  KEY idx_great_type_year (class_type, year),
  CONSTRAINT fk_great_stu_work FOREIGN KEY (stu_work_id) REFERENCES stu_work (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优秀作业展示';

CREATE TABLE equipment (
  id           BIGINT       NOT NULL AUTO_INCREMENT,
  name         VARCHAR(100) NOT NULL,
  photo_url    VARCHAR(500) DEFAULT NULL,
  info_url     VARCHAR(500) DEFAULT NULL,
  total_number INT          NOT NULL DEFAULT 0,
  used_number  INT          NOT NULL DEFAULT 0,
  status       TINYINT      NOT NULL DEFAULT 1,
  version      INT          NOT NULL DEFAULT 0,
  created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_equipment_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='实验器材';

CREATE TABLE equipment_borrow (
  id           BIGINT       NOT NULL AUTO_INCREMENT,
  equipment_id BIGINT       NOT NULL,
  stu_id       BIGINT       NOT NULL,
  stu_name     VARCHAR(50)  DEFAULT NULL,
  num          INT          NOT NULL,
  status       TINYINT      NOT NULL DEFAULT 0 COMMENT '0申请中 1已借出 2已归还 3已拒绝 4已取消',
  apply_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  approve_time DATETIME     DEFAULT NULL,
  return_time  DATETIME     DEFAULT NULL,
  operator_id  BIGINT       DEFAULT NULL,
  remark       VARCHAR(500) DEFAULT NULL,
  created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_borrow_equipment (equipment_id),
  KEY idx_borrow_student (stu_id),
  KEY idx_borrow_status (status),
  CONSTRAINT fk_borrow_equipment FOREIGN KEY (equipment_id) REFERENCES equipment (id),
  CONSTRAINT fk_borrow_student FOREIGN KEY (stu_id) REFERENCES student (stu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='器材借用记录';

CREATE TABLE lab_usage_log (
  id          BIGINT      NOT NULL AUTO_INCREMENT,
  stu_id      BIGINT      NOT NULL,
  class_id    BIGINT      DEFAULT NULL,
  borrow_id   BIGINT      DEFAULT NULL,
  event_type  VARCHAR(20) NOT NULL COMMENT 'BORROW/RETURN',
  occurred_at DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_usage_student (stu_id),
  KEY idx_usage_class (class_id),
  KEY idx_usage_time (occurred_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='实验室使用/出勤记录';

CREATE TABLE file_object (
  id            BIGINT       NOT NULL AUTO_INCREMENT,
  storage_type  VARCHAR(20)  NOT NULL DEFAULT 'local',
  storage_key   VARCHAR(500) NOT NULL,
  original_name VARCHAR(255) NOT NULL,
  content_type  VARCHAR(100) DEFAULT NULL,
  size_bytes    BIGINT       NOT NULL DEFAULT 0,
  url           VARCHAR(500) NOT NULL,
  uploader_id   BIGINT       DEFAULT NULL,
  created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_file_uploader (uploader_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件元数据';

CREATE TABLE login_log (
  id         BIGINT       NOT NULL AUTO_INCREMENT,
  user_type  VARCHAR(20)  NOT NULL,
  user_id    BIGINT       DEFAULT NULL,
  ip         VARCHAR(64)  DEFAULT NULL,
  success    TINYINT      NOT NULL DEFAULT 1,
  message    VARCHAR(255) DEFAULT NULL,
  created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_login_time (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录审计';
