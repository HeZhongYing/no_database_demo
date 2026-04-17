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

/**
 * 用户控制器
 * 提供用户的增删改查以及用户角色分配功能
 *
 * @author hezhongying
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Resource
    private UserRedisRepository userRedisRepository;

    @Resource
    private RoleRedisRepository roleRedisRepository;

    @Resource
    private UserRoleRelationRedisRepository userRoleRelationRedisRepository;

    /**
     * 获取所有用户列表
     *
     * @return 所有用户列表
     */
    @GetMapping
    public List<UserDO> getAllUsers() {
        log.info("获取所有用户列表");
        return userRedisRepository.getAll();
    }

    /**
     * 根据用户 ID 获取用户详情
     *
     * @param id 用户 ID
     * @return 用户详情
     */
    @GetMapping("/{id}")
    public UserDO getUserById(@PathVariable Long id) {
        log.info("获取用户详情，用户 ID: {}", id);
        return userRedisRepository.getById(id);
    }

    /**
     * 获取指定用户的所有角色
     *
     * @param userId 用户 ID
     * @return 角色列表
     */
    @GetMapping("/{userId}/roles")
    public List<RoleDO> getRolesByUserId(@PathVariable Long userId) {
        log.info("获取用户 {} 的所有角色", userId);
        List<Long> roleIds = userRoleRelationRedisRepository.getRoleIdsByUserId(userId);
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
     * 新增用户
     *
     * @param user 用户信息
     * @return 操作结果
     */
    @PostMapping
    public String addUser(@RequestBody UserDO user) {
        log.info("新增用户: {}", user.getUsername());
        if (user.getId() == null) {
            user.setId(System.currentTimeMillis());
        }
        if (user.getCreateTime() == null) {
            user.setCreateTime(LocalDateTime.now());
        }
        if (user.getDeleted() == null) {
            user.setDeleted(false);
        }
        userRedisRepository.save(user);
        return "用户添加成功";
    }

    /**
     * 更新用户信息
     *
     * @param id   用户 ID
     * @param user 用户信息
     * @return 操作结果
     */
    @PutMapping("/{id}")
    public String updateUser(@PathVariable Long id, @RequestBody UserDO user) {
        log.info("更新用户，用户 ID: {}", id);
        UserDO existingUser = userRedisRepository.getById(id);
        if (existingUser == null) {
            return "用户不存在";
        }
        user.setId(id);
        user.setUpdateTime(LocalDateTime.now());
        userRedisRepository.save(user);
        return "用户更新成功";
    }

    /**
     * 为用户分配角色
     *
     * @param userId  用户 ID
     * @param roleIds 角色 ID 列表
     * @return 操作结果
     */
    @PostMapping("/{userId}/roles")
    public String assignRolesToUser(@PathVariable Long userId, @RequestBody List<Long> roleIds) {
        log.info("为用户 {} 分配角色: {}", userId, roleIds);
        // 先删除该用户原有的角色关系
        userRoleRelationRedisRepository.deleteByUserId(userId);

        // 建立新的关系
        long relationId = System.currentTimeMillis();
        for (Long roleId : roleIds) {
            UserRoleRelationDO relation = new UserRoleRelationDO();
            relation.setId(relationId++);
            relation.setUserId(userId);
            relation.setRoleId(roleId);
            relation.setCreateTime(LocalDateTime.now());
            relation.setDeleted(false);
            userRoleRelationRedisRepository.save(relation);
        }
        return "角色分配成功";
    }

    /**
     * 删除用户
     *
     * @param id 用户 ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id) {
        log.info("删除用户，用户 ID: {}", id);
        // 先删除该用户的所有角色关系
        userRoleRelationRedisRepository.deleteByUserId(id);
        userRedisRepository.deleteById(id);
        return "用户删除成功";
    }

    /**
     * 清空所有用户
     *
     * @return 操作结果
     */
    @DeleteMapping("/clear")
    public String clearAllUsers() {
        log.info("清空所有用户");
        userRoleRelationRedisRepository.clearAll();
        userRedisRepository.clearAll();
        return "所有用户已清空";
    }

    /**
     * 创建示例用户数据
     *
     * @return 操作结果
     */
    @PostMapping("/sample")
    public String createSampleData() {
        log.info("创建示例用户数据");
        for (int i = 1; i <= 5; i++) {
            UserDO user = new UserDO();
            user.setId((long) i);
            user.setUsername("user" + i);
            user.setPassword("password" + i);
            user.setNickname("用户" + i);
            user.setUserType(1);
            user.setEmail("user" + i + "@example.com");
            user.setPhone("1380000000" + i);
            user.setStatus(1);
            user.setRemark("示例用户" + i);
            user.setCreateBy(1L);
            user.setCreateTime(LocalDateTime.now());
            user.setDeleted(false);
            userRedisRepository.save(user);
        }
        return "示例数据创建成功";
    }
}
