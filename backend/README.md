# 器材借用与课程作业管理系统 · 后端

基于 **Java 21 + Spring Boot 3.4 + MyBatis-Plus + MySQL 8** 的课程作业与实验器材借用管理系统后端。

## 功能

- 教师 / 学生 / 访客三种角色登录，JWT 认证
- 教师创建班级（物联网 / 嵌入式）、导入学生与小组
- 教师布置作业、查看小组提交、批改分数
- 学生按小组初始化作业、编辑富文本、上传视频/压缩包、提交
- 优秀作业按年份与课程类型展示
- 器材增删改查、学生申请借用、教师审批/拒绝/回收
- 学生/小组 Excel 批量导入，班级成绩 Excel 导出
- 作业 PDF 导出（含富文本、成员、视频二维码），按作业批量打包
- 文件本地上传，支持切换到 MinIO
- WebSocket 推送班级作业/小组/器材变更
- 定时刷新作业状态
- 班级、器材、实验室使用统计

## 目录

```text
backend/
├── pom.xml
├── sql/schema.sql          # 建库建表
├── sql/data.sql            # 演示数据
├── docker-compose.yml      # 可选 MySQL / Redis / MinIO
├── scripts/                # 本地启动脚本
└── src/main/java/com/zjgs/equip/
    ├── auth/               # 登录、JWT、TokenStore
    ├── clazz/              # 班级、小组、学生-班级关系、Excel 导入
    ├── user/               # 教师/学生/访客
    ├── homework/           # 教师作业、小组作业、批改、状态定时任务
    ├── greatwork/          # 优秀作业
    ├── equipment/          # 器材与借用状态机
    ├── file/               # 本地上传 / MinIO
    ├── export/             # Excel / PDF
    ├── stats/              # 统计
    └── ws/                 # WebSocket
```

## 快速开始

### 1. 启动 MySQL

项目默认使用 **3307** 端口，避免和你本机已有的 MySQL 冲突：

```bash
./scripts/start-local-mysql.sh
```

脚本会初始化一个独立数据目录（默认 `/Users/eric/tmp/equip-mysql-data`），创建 `equip` 用户和 `equip_management` 数据库，并导入表结构与演示数据。

如果你已经有可用的 MySQL 3306，也可以直接执行：

```bash
mysql -uroot -p < sql/schema.sql
mysql -uroot -p < sql/data.sql
export DB_URL="jdbc:mysql://localhost:3306/equip_management?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&useSSL=false"
export DB_USERNAME=equip
export DB_PASSWORD=equip123456
```

### 2. 启动后端

```bash
cd backend
mvn spring-boot:run
```

默认端口 `8090`。

- Swagger UI: http://localhost:8090/swagger-ui.html
- 健康检查: http://localhost:8090/actuator/health

### 3. 启动前端

```bash
cd ../mini/vue-element-plus-admin
corepack enable pnpm
pnpm install
pnpm dev
```

前端 `vite.config.ts` 已配置 `/api`、`/files`、`/ws` 代理到 `http://127.0.0.1:8090`。

## 演示账号

| 角色 | 账号 | 密码 |
|---|---|---|
| 教师 | `teacher01` | `123456` |
| 学生 | `2023000001` | `123456` |
| 访客 | 任意 11 位手机号 | 任意（首次登录自动创建） |

## 配置项

| 环境变量 | 默认值 | 说明 |
|---|---|---|
| `SERVER_PORT` | `8090` | 后端端口 |
| `DB_URL` | 见 `application-local.yml` | MySQL 连接串 |
| `DB_USERNAME` | `equip` | 数据库用户 |
| `DB_PASSWORD` | `equip123456` | 数据库密码 |
| `JWT_SECRET` | 内置开发密钥 | 生产环境必须替换 |
| `JWT_EXPIRATION` | `7200` | Token 有效期（秒） |
| `AUTH_STORE` | `memory` | `memory` 或 `redis` |
| `STORAGE_TYPE` | `local` | `local` 或 `minio` |
| `STORAGE_LOCAL_DIR` | `./data/uploads` | 本地文件目录 |
| `STORAGE_PUBLIC_BASE_URL` | `http://localhost:8090/files` | 文件访问前缀 |
| `MINIO_ENDPOINT` | `http://localhost:9000` | MinIO 地址 |
| `MINIO_ACCESS_KEY` | `minioadmin` | MinIO AK |
| `MINIO_SECRET_KEY` | `minioadmin` | MinIO SK |
| `MINIO_BUCKET` | `equip` | MinIO bucket |

### 启用 Redis

```bash
export AUTH_STORE=redis
export SPRING_PROFILES_ACTIVE=local,redis
mvn spring-boot:run
```

### 启用 MinIO

```bash
export STORAGE_TYPE=minio
export SPRING_PROFILES_ACTIVE=local,minio
mvn spring-boot:run
```

或者使用 `docker-compose up -d` 启动 MySQL / Redis / MinIO。

## 主要接口

统一响应：

```json
{ "code": 0, "message": "ok", "data": {} }
```

| 模块 | 接口 |
|---|---|
| 认证 | `POST /api/v1/auth/login`、`GET /api/v1/auth/me`、`POST /api/v1/auth/logout` |
| 班级 | `GET/POST /api/v1/classes`、`GET/PUT/DELETE /api/v1/classes/{id}` |
| 学生 | `GET/POST /api/v1/students`、`PUT/DELETE /api/v1/students/{stuId}` |
| 小组 | `GET/POST /api/v1/groups`、`PUT/DELETE /api/v1/groups/{id}` |
| 导入 | `POST /api/v1/imports/students`、`POST /api/v1/imports/groups` |
| 作业 | `GET/POST /api/v1/works`、`GET/PUT/DELETE /api/v1/works/{id}` |
| 提交 | `GET/PUT /api/v1/submissions/{id}`、`POST /api/v1/submissions/{id}/score` |
| 优秀作业 | `GET /api/v1/great-works`、`POST /api/v1/submissions/{id}/great-work` |
| 器材 | `GET/POST /api/v1/equipments`、`PUT/DELETE /api/v1/equipments/{id}` |
| 借用 | `POST /api/v1/equipments/{id}/borrows`、`POST /api/v1/borrows/{id}/approve` |
| 文件 | `POST /api/v1/files`、`GET /api/v1/files/{id}` |
| 导出 | `GET /api/v1/exports/classes/{id}/grades.xlsx`、`GET /api/v1/exports/submissions/{id}.pdf` |
| 统计 | `GET /api/v1/stats/classes/{id}`、`GET /api/v1/stats/equipments` |
| 实时 | `WS /ws`，主题 `/topic/classes/{classId}` |

完整参数与 DTO 以 Swagger UI 为准。

## 文件与 PDF 说明

- 上传接口返回可直接访问的 URL。
- 视频通过 `/files/**` 静态映射提供，支持 HTTP Range，可网页内嵌播放。
- PDF 使用 openhtmltopdf 生成。中文需要 CJK 字体，默认尝试读取 `resources/fonts/NotoSansSC-Regular.otf` 或系统 Noto/思源字体；若缺失会降级并记录警告。
  可自行下载 Noto Sans SC 放到 `src/main/resources/fonts/NotoSansSC-Regular.otf`。

## 测试

```bash
mvn test
```

单元测试覆盖作业状态计算、借用状态机、JWT 等核心逻辑。

## 前端首次加载性能

Vite 开发服务器在这个模板上首次加载曾经需要约 60 秒，原因不是浏览器，而是：

1. Vite 5.1 在 Node 24 下会爬取 `element-plus` / `echarts` / `wangeditor` 的完整静态依赖图，耗时约 60 秒。
2. UnoCSS 扫描了 `src/components/IconPicker/src/data/*.ts` 里的全部 Iconify 图标名，生成了约 1.8 MB 的图标 CSS。
3. ESLint 插件在每次模块转换时执行，进一步放大冷启动时间。

已做的优化：

- `optimizeDeps.noDiscovery = true`，显式列出实际依赖，跳过 60 秒的依赖爬取。
- UnoCSS 排除 IconPicker 图标数据目录，图标 CSS 从 1.8 MB 降到约 24 KB。
- 开发模式关闭 `vite-plugin-eslint`，构建时仍保留检查。
- 生产构建使用 `vite build` + Nginx 静态资源，首屏约 60–150 ms。

当前本机实测：开发服务器冷启动后首屏约 0.5–1 秒，热刷新约 100–200 ms；生产预览首屏约 60 ms。

## 部署

生产环境建议：

```bash
cd mini/vue-element-plus-admin
pnpm build:pro
```

将 `dist` 交给 Nginx，并按 `backend/deploy/nginx.conf.example` 反向代理 `/api`、`/files`、`/ws`。不要直接用 `vite preview` 暴露到公网。

## 安全注意

- 生产环境务必替换 `JWT_SECRET`、数据库密码、MinIO 密钥。
- `sql/data.sql` 中的演示账号密码仅为本地开发使用。
- 旧目录 `ObjectLibDB/` 是另一个“实验室环境与进出管理”项目，与本项目无关，且包含明文数据库配置，请勿复用或提交其凭据。
