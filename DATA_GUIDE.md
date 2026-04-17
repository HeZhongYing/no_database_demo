# Excel 测试数据生成指南

## 快速生成 Excel 测试数据

### 方式一：直接运行工具类（推荐）

在 IDE 中直接运行 `ExcelDataGenerator` 的 main 方法：

1. 找到文件：`src/main/java/com/hezy/util/ExcelDataGenerator.java`
2. 右键点击文件
3. 选择 "Run 'ExcelDataGenerator.main()'"

这将在项目根目录下的 `data/` 文件夹中生成以下 5 个 Excel 文件：

- `users.xlsx` - 用户数据
- `roles.xlsx` - 角色数据
- `permissions.xlsx` - 权限数据
- `user_roles.xlsx` - 用户角色关系数据
- `role_permissions.xlsx` - 角色权限关系数据

### 方式二：使用 Maven 命令运行

```bash
# 编译项目
mvn compile

# 运行主类（需要配置 exec 插件）
# 或者直接在 IDE 中运行更简单
```

---

## 生成的数据说明

### users.xlsx（5个用户）

| 用户ID | 用户名 | 昵称 | 邮箱 | 手机 | 状态 |
|--------|--------|------|------|------|------|
| 1 | admin | 系统管理员 | admin@example.com | 13800000001 | 1 |
| 2 | zhangsan | 张三 | zhangsan@example.com | 13800000002 | 1 |
| 3 | lisi | 李四 | lisi@example.com | 13800000003 | 1 |
| 4 | wangwu | 王五 | wangwu@example.com | 13800000004 | 1 |
| 5 | guest | 访客用户 | guest@example.com | 13800000005 | 1 |

### roles.xlsx（3个角色）

| 角色ID | 角色名 | 角色类型 | 描述 | 状态 |
|--------|--------|----------|------|------|
| 1 | 超级管理员 | 1 | 拥有系统所有权限的管理员角色 | 1 |
| 2 | 普通用户 | 2 | 登录系统的普通注册用户角色 | 1 |
| 3 | 访客 | 3 | 未登录或临时访客的角色 | 1 |

### permissions.xlsx（8个权限）

| 权限ID | 权限名 | 权限码 | 类型 | 描述 |
|--------|--------|--------|------|------|
| 1 | 用户查看 | user:view | 1 | 查看用户列表和用户详情 |
| 2 | 用户创建 | user:create | 1 | 创建新用户 |
| 3 | 用户编辑 | user:edit | 1 | 编辑用户信息 |
| 4 | 用户删除 | user:delete | 1 | 删除用户 |
| 5 | 角色查看 | role:view | 2 | 查看角色列表和角色详情 |
| 6 | 角色管理 | role:manage | 2 | 管理角色（创建、编辑、删除） |
| 7 | 权限查看 | permission:view | 3 | 查看权限列表和权限详情 |
| 8 | 权限管理 | permission:manage | 3 | 管理权限（创建、编辑、删除） |

### user_roles.xlsx（用户-角色关系）

| 关系ID | 用户ID | 角色ID |
|--------|--------|--------|
| 1 | 1 | 1 |
| 2 | 2 | 2 |
| 3 | 3 | 2 |
| 4 | 4 | 2 |
| 5 | 5 | 3 |

### role_permissions.xlsx（角色-权限关系）

| 关系ID | 角色ID | 权限ID |
|--------|--------|--------|
| 1-8 | 1 | 1-8 | （超级管理员拥有所有权限） |
| 9 | 2 | 1 |
| 10 | 2 | 5 |
| 11 | 2 | 7 |
| 12 | 3 | 1 |

---

## 使用流程

### 1. 生成 Excel 数据文件

运行 `ExcelDataGenerator.main()` 生成 `data/` 目录下的 5 个 Excel 文件。

### 2. 启动项目

运行 `App.java` 启动 Spring Boot 应用。

### 3. 验证数据加载

查看启动日志，应该能看到类似以下内容：

```
项目启动, 正在从外部文件中加载数据
--- 开始加载用户数据 ---
用户数据加载成功，共 5 条
--- 开始加载角色数据 ---
角色数据加载成功，共 3 条
--- 开始加载权限数据 ---
权限数据加载成功，共 8 条
--- 开始加载用户角色关系数据 ---
用户角色关系数据加载成功，共 5 条
--- 开始加载角色权限关系数据 ---
角色权限关系数据加载成功，共 12 条
所有数据加载完成
```

### 4. 测试 API

```bash
# 查询所有用户
curl http://localhost:8888/api/users

# 查询 admin 的角色
curl http://localhost:8888/api/users/1/roles

# 查询超级管理员的权限
curl http://localhost:8888/api/permissions/by-role/1
```

### 5. 停止项目，验证数据保存

停止应用后，检查 `data/` 目录下的 Excel 文件是否被更新（时间戳会变化）。

---

## 自定义数据

如果需要修改测试数据，可以编辑 `ExcelDataGenerator.java` 文件中的数据，然后重新运行即可。

或者可以直接编辑生成的 Excel 文件，修改后重启项目会重新加载修改后的数据。
