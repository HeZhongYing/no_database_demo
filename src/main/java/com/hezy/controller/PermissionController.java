package com.hezy.controller;

import com.hezy.dal.redis.PermissionRedisRepository;
import com.hezy.dal.redis.RolePermissionRelationRedisRepository;
import com.hezy.dal.redis.RoleRedisRepository;
import com.hezy.pojo.PermissionDO;
import com.hezy.pojo.RoleDO;
import com.hezy.pojo.RolePermissionRelationDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 权限控制器
 * 提供权限的增删改查以及角色权限分配功能
 *
 * @author hezhongying
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    @Resource
    private PermissionRedisRepository permissionRedisRepository;

    @Resource
    private RoleRedisRepository roleRedisRepository;

    @Resource
    private RolePermissionRelationRedisRepository rolePermissionRelationRedisRepository;

    /**
     * 获取所有权限列表
     *
     * @return 所有权限列表
     */
    @GetMapping
    public List<PermissionDO> getAllPermissions() {
        log.info("获取所有权限列表");
        return permissionRedisRepository.getAll();
    }

    /**
     * 根据权限 ID 获取权限详情
     *
     * @param id 权限 ID
     * @return 权限详情
     */
    @GetMapping("/{id}")
    public PermissionDO getPermissionById(@PathVariable Long id) {
        log.info("获取权限详情，权限 ID: {}", id);
        return permissionRedisRepository.getById(id);
    }

    /**
     * 新增权限
     *
     * @param permission 权限信息
     * @return 操作结果
     */
    @PostMapping
    public String addPermission(@RequestBody PermissionDO permission) {
        log.info("新增权限: {}", permission.getName());
        if (permission.getId() == null) {
            permission.setId(System.currentTimeMillis());
        }
        if (permission.getCreateTime() == null) {
            permission.setCreateTime(LocalDateTime.now());
        }
        if (permission.getDeleted() == null) {
            permission.setDeleted(false);
        }
        permissionRedisRepository.save(permission);
        return "权限添加成功";
    }

    /**
     * 更新权限信息
     *
     * @param id         权限 ID
     * @param permission 权限信息
     * @return 操作结果
     */
    @PutMapping("/{id}")
    public String updatePermission(@PathVariable Long id, @RequestBody PermissionDO permission) {
        log.info("更新权限，权限 ID: {}", id);
        PermissionDO existingPermission = permissionRedisRepository.getById(id);
        if (existingPermission == null) {
            return "权限不存在";
        }
        permission.setId(id);
        permission.setUpdateTime(LocalDateTime.now());
        permissionRedisRepository.save(permission);
        return "权限更新成功";
    }

    /**
     * 删除权限
     *
     * @param id 权限 ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public String deletePermission(@PathVariable Long id) {
        log.info("删除权限，权限 ID: {}", id);
        // 先删除该权限的所有角色关系
        rolePermissionRelationRedisRepository.deleteByPermissionId(id);
        permissionRedisRepository.deleteById(id);
        return "权限删除成功";
    }

    /**
     * 清空所有权限
     *
     * @return 操作结果
     */
    @DeleteMapping("/clear")
    public String clearAllPermissions() {
        log.info("清空所有权限");
        rolePermissionRelationRedisRepository.clearAll();
        permissionRedisRepository.clearAll();
        return "所有权限已清空";
    }

    /**
     * 获取指定权限的所有角色
     *
     * @param permissionId 权限 ID
     * @return 角色列表
     */
    @GetMapping("/{permissionId}/roles")
    public List<RoleDO> getRolesByPermissionId(@PathVariable Long permissionId) {
        log.info("获取权限 {} 的所有角色", permissionId);
        List<Long> roleIds = rolePermissionRelationRedisRepository.getRoleIdsByPermissionId(permissionId);
        List<RoleDO> roles = new ArrayList<>();
        for (Long roleId : roleIds) {
            RoleDO role = roleRedisRepository.getById(roleId);
            if (role != null) {
                roles.add(role);
            }
        }
        return roles;
    }

    /**
     * 获取指定角色的所有权限
     *
     * @param roleId 角色 ID
     * @return 权限列表
     */
    @GetMapping("/by-role/{roleId}")
    public List<PermissionDO> getPermissionsByRoleId(@PathVariable Long roleId) {
        log.info("获取角色 {} 的所有权限", roleId);
        List<Long> permissionIds = rolePermissionRelationRedisRepository.getPermissionIdsByRoleId(roleId);
        List<PermissionDO> permissions = new ArrayList<>();
        for (Long permissionId : permissionIds) {
            PermissionDO permission = permissionRedisRepository.getById(permissionId);
            if (permission != null) {
                permissions.add(permission);
            }
        }
        return permissions;
    }

    /**
     * 为角色分配权限
     *
     * @param roleId        角色 ID
     * @param permissionIds 权限 ID 列表
     * @return 操作结果
     */
    @PostMapping("/by-role/{roleId}")
    public String assignPermissionsToRole(@PathVariable Long roleId, @RequestBody List<Long> permissionIds) {
        log.info("为角色 {} 分配权限: {}", roleId, permissionIds);
        // 先删除该角色原有的权限关系
        rolePermissionRelationRedisRepository.deleteByRoleId(roleId);

        // 建立新的关系
        long relationId = System.currentTimeMillis();
        for (Long permissionId : permissionIds) {
            RolePermissionRelationDO relation = new RolePermissionRelationDO();
            relation.setId(relationId++);
            relation.setRoleId(roleId);
            relation.setPermissionId(permissionId);
            relation.setCreateTime(LocalDateTime.now());
            relation.setDeleted(false);
            rolePermissionRelationRedisRepository.save(relation);
        }
        return "权限分配成功";
    }

    /**
     * 创建示例权限数据
     *
     * @return 操作结果
     */
    @PostMapping("/sample")
    public String createSampleData() {
        log.info("创建示例权限数据");
        String[] permNames = {"用户查看", "用户创建", "用户编辑", "用户删除", "角色查看", "角色管理", "权限查看", "权限管理"};
        String[] permCodes = {"user:view", "user:create", "user:edit", "user:delete", "role:view", "role:manage", "permission:view", "permission:manage"};
        Integer[] permTypes = {1, 1, 1, 1, 2, 2, 3, 3};
        String[] descriptions = {
                "查看用户列表和详情", "创建新用户", "编辑用户信息", "删除用户",
                "查看角色列表和详情", "管理角色（增删改）", "查看权限列表和详情", "管理权限（增删改）"
        };

        long id = 1;
        for (int i = 0; i < permNames.length; i++) {
            PermissionDO permission = new PermissionDO();
            permission.setId(id++);
            permission.setName(permNames[i]);
            permission.setCode(permCodes[i]);
            permission.setType(permTypes[i]);
            permission.setDescription(descriptions[i]);
            permission.setCreateBy(1L);
            permission.setCreateTime(LocalDateTime.now());
            permission.setDeleted(false);
            permissionRedisRepository.save(permission);
        }
        return "示例权限数据创建成功";
    }
}
