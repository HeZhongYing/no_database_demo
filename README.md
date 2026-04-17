## 介绍
这是一个不需要数据库支持的系统Demo，可以当作一次没有数据库支持系统可行性的尝试，或者当作一次系统是否必须需要数据库支持的讨论。

## 设计思路
简单来说，在系统启动时，将数据加载到本地缓存或Redis中，系统停止时，将数据持久化到文件中。

## 功能特性

- ✅ 基于 Excel 文件的数据存储
- ✅ 启动时自动从 Excel 加载数据到 Redis
- ✅ 停机时自动从 Redis 保存数据到 Excel
- ✅ 提供 RESTful API 进行用户数据管理
- ✅ 使用 EasyExcel 高效读写 Excel
- ✅ 使用 Redis 作为数据缓存层

## 项目结构

```
src/main/java/com/hezy/
├── App.java                      # 启动类
├── config/
│   └── RedisCacheConfig.java     # Redis 配置
├── controller/
│   ├── DemoController.java       # 示例控制器
│   └── UserController.java       # 用户控制器
├── dal/redis/
│   └── UserRedisRepository.java  # 用户 Redis 数据访问层
├── interceptor/
│   ├── InitInterceptor.java      # 启动拦截器
│   └── FinishedInterceptor.java  # 停机拦截器
├── pojo/
│   ├── BaseDO.java               # 基础实体
│   └── UserDO.java               # 用户实体
└── service/
    ├── FileHandlerService.java   # 文件处理接口
    ├── AbstractFileHandlerService.java
    └── impl/
        └── ExcelFileHandlerServiceImpl.java  # Excel 实现
```

## 快速开始

### 前置要求

- JDK 17+
- Redis 6.0+

### 配置

修改 `src/main/resources/application.yml` 中的 Redis 配置：

```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password: ""
    database: 0
```

### 运行

1. 启动 Redis 服务
2. 运行 `App.java` 启动 Spring Boot 应用

### API 使用

#### 创建示例数据
```bash
POST http://localhost:8888/api/users/sample
```

#### 获取所有用户
```bash
GET http://localhost:8888/api/users
```

#### 新增用户
```bash
POST http://localhost:8888/api/users
Content-Type: application/json

{
  "id": 1,
  "username": "test",
  "password": "123456",
  "nickname": "测试用户",
  "email": "test@example.com",
  "phone": "13800138000",
  "status": 1
}
```

#### 删除用户
```bash
DELETE http://localhost:8888/api/users/{id}
```

## 数据流程

1. **启动阶段**
   - `InitInterceptor` 监听 `ContextRefreshedEvent`
   - 读取 `data/users.xlsx` 文件
   - 将数据写入 Redis

2. **运行阶段**
   - 所有数据操作通过 `UserRedisRepository` 进行
   - 直接读写 Redis，性能高效

3. **停机阶段**
   - `FinishedInterceptor` 监听 `ContextClosedEvent`
   - 从 Redis 读取所有数据
   - 写入 `data/users.xlsx` 文件

## 技术栈

- Spring Boot 2.7.12
- Spring Data Redis
- EasyExcel 4.0.3
- Lombok
- Hutool
- Fastjson2