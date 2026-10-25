# 个人健康与运动数据中心

面向个人用户的一站式健康与运动数据管理平台，覆盖数据总览、健康记录、运动记录、健康目标、个人中心五大核心业务，提供步数、卡路里、睡眠、饮水、心率、体重等多维数据的记录、统计与趋势可视化，帮助用户全面掌握自身健康状态、科学制定并跟踪健康目标。

## 功能模块

| 模块 | 说明 |
|---|---|
| 数据总览仪表盘 | 本周总步数 / 总卡路里 / 运动次数 / 平均睡眠统计卡片，步数与卡路里趋势双轴图、睡眠趋势面积图、今日健康环形图、运动类型占比饼图 |
| 健康数据管理 | 每日健康记录增删改查（日期、步数、卡路里、距离、心率、睡眠时长 / 质量、饮水量、体重、备注），同一日期自动去重覆盖 |
| 运动记录管理 | 运动记录增删改查（跑步 / 骑行 / 游泳 / 步行 / 瑜伽 / 健身等类型、时长、距离、卡路里、平均 / 最大心率、强度等级），近 30 天统计与类型分布 |
| 健康目标管理 | 健康目标创建 / 编辑 / 删除（步数、卡路里、运动次数、减重等），目标进度自动计算，进度条按完成度变色 |
| 个人中心 | 个人资料查看与编辑（昵称、性别、年龄、身高、体重、手机、邮箱），BMI 自动计算与健康状态评估 |

## 技术栈

**前端**
- Vue 3.4（Composition API + `<script setup>`）
- Vite 5.2 构建工具
- Vue Router 4（路由 + 登录守卫）
- Pinia 2（状态管理，localStorage 持久化）
- Element Plus 2.7（UI 组件库，中文语言包）
- ECharts 5.5（数据可视化图表）
- Axios 1.6（请求封装、Token 自动注入、401 自动登出）

**后端**
- Spring Boot 2.7
- MyBatis-Plus 3.5（ORM 数据访问）
- MySQL 8.0（数据存储）
- JWT 登录认证（Token 有效期 7 天）
- BCrypt 密码加密
- Lombok

## 项目结构

```
health-fitness-center/
├── sql/
│   └── init.sql                      # 数据库初始化脚本（建库、建表、测试数据）
├── backend/                          # 后端 Spring Boot 工程
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/health/
│       │   ├── HealthFitnessApplication.java   # 启动类
│       │   ├── common/
│       │   │   └── Result.java                 # 统一响应封装
│       │   ├── config/                         # JWT 拦截器、Web 配置、字段自动填充
│       │   ├── controller/                     # 用户 / 健康 / 运动 / 目标 / 仪表盘接口
│       │   ├── dto/                            # 登录请求 DTO
│       │   ├── entity/                         # User、HealthRecord、ExerciseRecord、HealthGoal
│       │   ├── mapper/                         # MyBatis-Plus Mapper 接口
│       │   ├── service/                        # 业务接口与实现类
│       │   └── utils/
│       │       └── JwtUtil.java                # JWT 生成与解析工具
│       └── resources/
│           └── application.yml                 # 端口 8081、数据源、JWT 配置
└── frontend/                         # 前端 Vue3 工程
    ├── index.html
    ├── package.json
    ├── vite.config.js                # 端口 5175，/api 代理到 8081
    └── src/
        ├── main.js
        ├── App.vue
        ├── router/                   # 路由与登录守卫
        ├── stores/user.js            # 用户状态（Pinia）
        ├── utils/request.js          # Axios 封装
        ├── components/Layout.vue     # 整体布局（侧边栏 + 顶栏）
        └── views/                    # 登录 / 注册 / 仪表盘 / 健康数据 / 运动记录 / 健康目标 / 个人中心
```

## 运行指南

**环境要求**
- JDK 11+、Maven 3.6+
- Node.js 16+
- MySQL 8.0+

**1. 初始化数据库**

```bash
mysql -u root -p < sql/init.sql
```

**2. 启动后端**

```bash
cd backend
# 按需修改 src/main/resources/application.yml 中的数据库账号密码
mvn spring-boot:run
```

后端运行在 http://localhost:8081

**3. 启动前端**

```bash
cd frontend
npm install
npm run dev
```

前端运行在 http://localhost:5175

**4. 测试账号**
- 用户名：`test`
- 密码：`123456`
