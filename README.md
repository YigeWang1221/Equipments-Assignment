# 器材借用与课程作业管理系统

一个面向高校实验课程的器材借用与作业管理系统。前端为 Vue 3 + Element Plus，后端为 Java 21 + Spring Boot 3.4 + MyBatis-Plus + MySQL 8。

## 目录

- `mini/vue-element-plus-admin`：Vue 3 前端
- `backend/`：Spring Boot 后端、MySQL 表结构、演示数据、部署脚本
- `ObjectLibDB/`：早期另一个“实验室环境与进出管理”项目，与本项目无关，保留作为历史资料

## 快速启动

```bash
# 1. 启动独立 MySQL（3307，不影响本机已有 MySQL）
cd backend
./scripts/start-local-mysql.sh

# 2. 启动后端（8090）
mvn spring-boot:run

# 3. 启动前端（4000）
cd ../mini/vue-element-plus-admin
corepack enable pnpm
pnpm install
pnpm dev
```

详细说明见 `backend/README.md`。

## 演示账号

- 教师：`teacher01` / `123456`
- 学生：`2023000001` / `123456`
- 访客：任意 11 位手机号，首次登录自动创建

## 主要功能

- 教师/学生/访客 JWT 登录与三级权限
- 班级、小组、学生管理，Excel 批量导入
- 教师作业发布、学生小组作业提交、教师批改与优秀作业展示
- 实验器材管理、借用申请、审批、拒绝、归还
- 班级成绩 Excel 导出、作业报告 PDF 导出与批量打包
- 本地上传 / MinIO，WebSocket 实时推送，实验室使用统计
