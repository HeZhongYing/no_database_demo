package com.hezy.controller;

import com.hezy.dal.redis.RoleRedisRepository;
import com.hezy.dal.redis.UserRoleRelationRedisRepository;
import com.hezy.dal.redis.UserRedisRepository;
import com.hezy.pojo.RoleDO;
import com.hezy.pojo.UserDO;
import com.hezy.pojo.UserRoleRelationDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色控制器
 * 提供角色的增删改查以及用户角色分配功能
 *
 * @author hezhongying
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Resource
    private RoleRedisRepository roleRedisRepository;

    @Resource
    private UserRedisRepository userRedisRepository;

    @Resource
    private UserRoleRelationRedisRepository userRoleRelationRedisRepository;

    /**
     * 获取所有角色列表
     *
     * @return 所有角色列表
     */
    @GetMapping
    public List<RoleDO> getAllRoles() {
        log.info("获取所有角色列表");
        return roleRedisRepository.getAll();
    }

    /**
     * 根据角色 ID 获取角色详情
     *
     * @param id 角色 ID
     * @return 角色详情
     */
    @GetMapping("/{id}")
    public RoleDO getRoleById(@PathVariable Long id) {
        log.info("获取角色详情，角色 ID: {}", id);
        return roleRedisRepository.getById(id);
    }

    /**
     * 新增角色
     *
     * @param role 角色信息
     * @return 操作结果
     */
    @PostMapping
    public String addRole(@RequestBody RoleDO role) {
        log.info("新增角色: {}", role.getName());
        if (role.getId() == null) {
            role.setId(System.currentTimeMillis());
        }
        if (role.getCreateTime() == null) {
            role.setCreateTime(LocalDateTime.now());
        }
        if (role.getDeleted() == null) {
            role.setDeleted(false);
        }
        roleRedisRepository.save(role);
        return "角色添加成功";
    }

    /**
     * 更新角色信息
     *
     * @param id   角色 ID
     * @param role 角色信息
     * @return 操作结果
     */
    @PutMapping("/{id}")
    public String updateRole(@PathVariable Long id, @RequestBody RoleDO role) {
        log.info("更新角色，角色 ID: {}", id);
        RoleDO existingRole = roleRedisRepository.getById(id);
        if (existingRole == null) {
            return "角色不存在";
        }
        role.setId(id);
        role.setUpdateTime(LocalDateTime.now());
        roleRedisRepository.save(role);
        return "角色更新成功";
    }

    /**
     * 删除角色
     *
     * @param id 角色 ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public String deleteRole(@PathVariable Long id) {
        log.info("删除角色，角色 ID: {}", id);
        // 先删除该角色的所有用户关系
        userRoleRelationRedisRepository.deleteByRoleId(id);
        roleRedisRepository.deleteById(id);
        return "角色删除成功";
    }

    /**
     * 清空所有角色
     *
     * @return 操作结果
     */
    @DeleteMapping("/clear")
    public String clearAllRoles() {
        log.info("清空所有角色");
        userRoleRelationRedisRepository.clearAll();
        roleRedisRepository.clearAll();
        return "所有角色已清空";
    }

    /**
     * 获取指定角色的所有用户
     *
     * @param roleId 角色 ID
     * @return 用户列表
     */
    @GetMapping("/{roleId}/users")
    public List<UserDO> getUsersByRoleId(@PathVariable Long roleId) {
        log.info("获取角色 {} 的所有用户", roleId);
        List<Long> userIds = userRoleRelationRedisRepository.getUserIdsByRoleId(roleId);
        List<UserDO> users = new ArrayList<>();
        for (Long userId : userIds) {
            UserDO user = userRedisRepository.getById(userId);
            if (user != null) {
                users.add(user);
            }
        }
        return users;
    }

    /**
     * 为角色分配用户
     *
     * @param roleId  角色 ID
     * @param userIds 用户 ID 列表
     * @return 操作结果
     */
    @PostMapping("/{roleId}/users")
    public String assignUsersToRole(@PathVariable Long roleId, @RequestBody List<Long> userIds) {
        log.info("为角色 {} 分配用户: {}", roleId, userIds);
        // 先删除该角色原有的用户关系
        userRoleRelationRedisRepository.deleteByRoleId(roleId);

        // 建立新的关系
        long relationId = System.currentTimeMillis();
        for (Long userId : userIds) {
            UserRoleRelationDO relation = new UserRoleRelationDO();
            relation.setId(relationId++);
            relation.setUserId(userId);
            relation.setRoleId(roleId);
            relation.setCreateTime(LocalDateTime.now());
            relation.setDeleted(false);
            userRoleRelationRedisRepository.save(relation);
        }
        return "用户分配成功";
    }

    /**
     * 创建示例角色数据
     *
     * @return 操作结果
     */
    @PostMapping("/sample")
    public String createSampleData() {
        log.info("创建示例角色数据");
        String[] roleNames = {"管理员", "普通用户", "访客"};
        String[] descriptions = {"系统管理员，拥有所有权限", "普通注册用户", "未注册的访客用户"};
        Integer[] roleTypes = {1, 2, 3};

        long id = 1;
        for (int i = 0; i < roleNames.length; i++) {
            RoleDO role = new RoleDO();
            role.setId(id++);
            role.setName(roleNames[i]);
            role.setRoleType(roleTypes[i]);
            role.setDescription(descriptions[i]);
            role.setStatus(1);
            role.setCreateBy(1L);
            role.setCreateTime(LocalDateTime.now());
            role.setDeleted(false);
            roleRedisRepository.save(role);
        }
        return "示例角色数据创建成功";
    }
}
