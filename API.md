# API 接口文档

## 基础信息

- **基础路径**: `http://localhost:8888`
- **数据格式**: JSON

---

## 初始化数据接口 (`/api/init`)

### 1. 一键初始化所有测试数据
- **接口**: `POST /api/init/all`
- **描述**: 一键创建完整的测试数据，包括：
  - 5个用户（admin、zhangsan、lisi、wangwu、guest）
  - 3个角色（超级管理员、普通用户、访客）
  - 8个权限（用户管理、角色管理、权限管理相关）
  - 用户-角色关联关系
  - 角色-权限关联关系
- **响应**: 操作结果字符串

### 2. 清空所有数据
- **接口**: `DELETE /api/init/all`
- **描述**: 清空所有数据（用户、角色、权限及关联关系）
- **响应**: 操作结果字符串

---

## 用户管理接口 (`/api/users`)

### 1. 获取所有用户
- **接口**: `GET /api/users`
- **描述**: 获取所有用户列表
- **响应**: 用户列表 JSON 数组

### 2. 获取用户详情
- **接口**: `GET /api/users/{id}`
- **描述**: 根据用户 ID 获取用户详情
- **路径参数**:
  - `id` - 用户 ID
- **响应**: 用户详情 JSON 对象

### 3. 获取用户的角色列表
- **接口**: `GET /api/users/{userId}/roles`
- **描述**: 获取指定用户拥有的所有角色
- **路径参数**:
  - `userId` - 用户 ID
- **响应**: 角色列表 JSON 数组

### 4. 新增用户
- **接口**: `POST /api/users`
- **描述**: 创建新用户
- **请求体**:
```json
{
  "username": "zhangsan",
  "password": "123456",
  "nickname": "张三",
  "email": "zhangsan@example.com",
  "phone": "13800138000",
  "status": 1
}
```
- **响应**: 操作结果字符串

### 5. 更新用户
- **接口**: `PUT /api/users/{id}`
- **描述**: 更新用户信息
- **路径参数**:
  - `id` - 用户 ID
- **请求体**: 用户信息 JSON 对象
- **响应**: 操作结果字符串

### 6. 为用户分配角色
- **接口**: `POST /api/users/{userId}/roles`
- **描述**: 为用户分配一个或多个角色
- **路径参数**:
  - `userId` - 用户 ID
- **请求体**: 角色 ID 数组
```json
[1, 2, 3]
```
- **响应**: 操作结果字符串

### 7. 删除用户
- **接口**: `DELETE /api/users/{id}`
- **描述**: 删除指定用户（同时会删除用户的角色关系）
- **路径参数**:
  - `id` - 用户 ID
- **响应**: 操作结果字符串

### 8. 清空所有用户
- **接口**: `DELETE /api/users/clear`
- **描述**: 清空所有用户数据
- **响应**: 操作结果字符串

### 9. 创建示例用户数据
- **接口**: `POST /api/users/sample`
- **描述**: 创建 5 个示例用户数据
- **响应**: 操作结果字符串

---

## 角色管理接口 (`/api/roles`)

### 1. 获取所有角色
- **接口**: `GET /api/roles`
- **描述**: 获取所有角色列表
- **响应**: 角色列表 JSON 数组

### 2. 获取角色详情
- **接口**: `GET /api/roles/{id}`
- **描述**: 根据角色 ID 获取角色详情
- **路径参数**:
  - `id` - 角色 ID
- **响应**: 角色详情 JSON 对象

### 3. 获取角色的用户列表
- **接口**: `GET /api/roles/{roleId}/users`
- **描述**: 获取拥有指定角色的所有用户
- **路径参数**:
  - `roleId` - 角色 ID
- **响应**: 用户列表 JSON 数组

### 4. 新增角色
- **接口**: `POST /api/roles`
- **描述**: 创建新角色
- **请求体**:
```json
{
  "name": "编辑",
  "roleType": 2,
  "description": "内容编辑人员",
  "status": 1
}
```
- **响应**: 操作结果字符串

### 5. 更新角色
- **接口**: `PUT /api/roles/{id}`
- **描述**: 更新角色信息
- **路径参数**:
  - `id` - 角色 ID
- **请求体**: 角色信息 JSON 对象
- **响应**: 操作结果字符串

### 6. 为角色分配用户
- **接口**: `POST /api/roles/{roleId}/users`
- **描述**: 为角色分配一个或多个用户
- **路径参数**:
  - `roleId` - 角色 ID
- **请求体**: 用户 ID 数组
```json
[1, 2, 3]
```
- **响应**: 操作结果字符串

### 7. 删除角色
- **接口**: `DELETE /api/roles/{id}`
- **描述**: 删除指定角色（同时会删除角色的用户关系）
- **路径参数**:
  - `id` - 角色 ID
- **响应**: 操作结果字符串

### 8. 清空所有角色
- **接口**: `DELETE /api/roles/clear`
- **描述**: 清空所有角色数据
- **响应**: 操作结果字符串

### 9. 创建示例角色数据
- **接口**: `POST /api/roles/sample`
- **描述**: 创建示例角色数据（管理员、普通用户、访客）
- **响应**: 操作结果字符串

---

## 权限管理接口 (`/api/permissions`)

### 1. 获取所有权限
- **接口**: `GET /api/permissions`
- **描述**: 获取所有权限列表
- **响应**: 权限列表 JSON 数组

### 2. 获取权限详情
- **接口**: `GET /api/permissions/{id}`
- **描述**: 根据权限 ID 获取权限详情
- **路径参数**:
  - `id` - 权限 ID
- **响应**: 权限详情 JSON 对象

### 3. 获取权限的角色列表
- **接口**: `GET /api/permissions/{permissionId}/roles`
- **描述**: 获取拥有指定权限的所有角色
- **路径参数**:
  - `permissionId` - 权限 ID
- **响应**: 角色列表 JSON 数组

### 4. 获取角色的权限列表
- **接口**: `GET /api/permissions/by-role/{roleId}`
- **描述**: 获取指定角色拥有的所有权限
- **路径参数**:
  - `roleId` - 角色 ID
- **响应**: 权限列表 JSON 数组

### 5. 新增权限
- **接口**: `POST /api/permissions`
- **描述**: 创建新权限
- **请求体**:
```json
{
  "name": "文章发布",
  "code": "article:publish",
  "type": 1,
  "description": "发布文章权限"
}
```
- **响应**: 操作结果字符串

### 6. 更新权限
- **接口**: `PUT /api/permissions/{id}`
- **描述**: 更新权限信息
- **路径参数**:
  - `id` - 权限 ID
- **请求体**: 权限信息 JSON 对象
- **响应**: 操作结果字符串

### 7. 为角色分配权限
- **接口**: `POST /api/permissions/by-role/{roleId}`
- **描述**: 为角色分配一个或多个权限
- **路径参数**:
  - `roleId` - 角色 ID
- **请求体**: 权限 ID 数组
```json
[1, 2, 3]
```
- **响应**: 操作结果字符串

### 8. 删除权限
- **接口**: `DELETE /api/permissions/{id}`
- **描述**: 删除指定权限（同时会删除权限的角色关系）
- **路径参数**:
  - `id` - 权限 ID
- **响应**: 操作结果字符串

### 9. 清空所有权限
- **接口**: `DELETE /api/permissions/clear`
- **描述**: 清空所有权限数据
- **响应**: 操作结果字符串

### 10. 创建示例权限数据
- **接口**: `POST /api/permissions/sample`
- **描述**: 创建示例权限数据（用户管理、角色管理、权限管理等）
- **响应**: 操作结果字符串

---

## 快速使用示例

### 方式一：使用 curl 命令（推荐）

```bash
# 1. 一键初始化完整测试数据（推荐）
curl -X POST http://localhost:8888/api/init/all

# 2. 查询所有数据
curl http://localhost:8888/api/users
curl http://localhost:8888/api/roles
curl http://localhost:8888/api/permissions

# 3. 查询用户 admin (ID=1) 的角色
curl http://localhost:8888/api/users/1/roles

# 4. 查询超级管理员角色 (ID=1) 的权限
curl http://localhost:8888/api/permissions/by-role/1

# 5. 查询普通用户角色 (ID=2) 的权限
curl http://localhost:8888/api/permissions/by-role/2

# 6. 停止应用，检查 data/ 目录下生成的 Excel 文件
```

### 方式二：分步创建（如果需要自定义）

```bash
# 1. 清空所有数据
curl -X DELETE http://localhost:8888/api/init/all

# 2. 分别创建示例数据
curl -X POST http://localhost:8888/api/users/sample
curl -X POST http://localhost:8888/api/roles/sample
curl -X POST http://localhost:8888/api/permissions/sample

# 3. 为用户 1 分配角色 1
curl -X POST -H "Content-Type: application/json" -d "[1]" http://localhost:8888/api/users/1/roles

# 4. 为角色 1 分配所有权限
curl -X POST -H "Content-Type: application/json" -d "[1,2,3,4,5,6,7,8]" http://localhost:8888/api/permissions/by-role/1
```

### 方式三：使用 Postman 或 Apifox

1. **一键初始化（推荐）**:
   - `POST /api/init/all`

2. **验证数据**:
   - `GET /api/users` - 查看 5 个用户
   - `GET /api/roles` - 查看 3 个角色
   - `GET /api/permissions` - 查看 8 个权限
   - `GET /api/users/1/roles` - 查看 admin 的角色
   - `GET /api/permissions/by-role/1` - 查看超级管理员的权限

3. **停止应用，检查 `data/` 目录下生成的 5 个 Excel 文件**

---

## 测试数据说明

### 用户数据 (5个)

| ID | 用户名 | 昵称 | 角色 | 说明 |
|----|--------|------|------|------|
| 1 | admin | 系统管理员 | 超级管理员 | 拥有所有权限 |
| 2 | zhangsan | 张三 | 普通用户 | 运营部门员工 |
| 3 | lisi | 李四 | 普通用户 | 技术部门员工 |
| 4 | wangwu | 王五 | 普通用户 | 市场部门员工 |
| 5 | guest | 访客用户 | 访客 | 临时访客账户 |

### 角色数据 (3个)

| ID | 角色名 | 类型 | 权限范围 |
|----|--------|------|----------|
| 1 | 超级管理员 | 1 | 所有权限 (1-8) |
| 2 | 普通用户 | 2 | 查看权限 (1,5,7) |
| 3 | 访客 | 3 | 仅用户查看 (1) |

### 权限数据 (8个)

| ID | 权限名 | 权限码 | 类型 | 说明 |
|----|--------|--------|------|------|
| 1 | 用户查看 | user:view | 1 | 查看用户列表和详情 |
| 2 | 用户创建 | user:create | 1 | 创建新用户 |
| 3 | 用户编辑 | user:edit | 1 | 编辑用户信息 |
| 4 | 用户删除 | user:delete | 1 | 删除用户 |
| 5 | 角色查看 | role:view | 2 | 查看角色列表和详情 |
| 6 | 角色管理 | role:manage | 2 | 管理角色（增删改） |
| 7 | 权限查看 | permission:view | 3 | 查看权限列表和详情 |
| 8 | 权限管理 | permission:manage | 3 | 管理权限（增删改） |

### 用户-角色关系

| 用户 | 角色 |
|------|------|
| admin | 超级管理员 |
| zhangsan | 普通用户 |
| lisi | 普通用户 |
| wangwu | 普通用户 |
| guest | 访客 |

### 角色-权限关系

| 角色 | 权限 |
|------|------|
| 超级管理员 | 1,2,3,4,5,6,7,8 (所有) |
| 普通用户 | 1,5,7 (查看类) |
| 访客 | 1 (仅用户查看) |
