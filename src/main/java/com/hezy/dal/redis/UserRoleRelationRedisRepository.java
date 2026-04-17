package com.hezy.dal.redis;

import com.alibaba.fastjson2.JSON;
import com.hezy.pojo.UserRoleRelationDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 用户角色关系 Redis 数据访问层
 * 提供用户角色关系数据的 Redis 增删改查操作
 *
 * @author hezhongying
 * @version 1.0.0
 */
@Slf4j
@Repository
public class UserRoleRelationRedisRepository {

    /**
     * 用户角色关系数据 Redis Key 前缀
     */
    private static final String USER_ROLE_KEY_PREFIX = "user_role:";

    /**
     * 用户角色关系 ID 列表 Redis Key
     */
    private static final String USER_ROLE_LIST_KEY = "user_role:list";

    /**
     * 用户ID -> 角色ID列表的索引 Key 前缀
     */
    private static final String USER_ID_ROLES_PREFIX = "user_id_roles:";

    /**
     * 角色ID -> 用户ID列表的索引 Key 前缀
     */
    private static final String ROLE_ID_USERS_PREFIX = "role_id_users:";

    /**
     * 默认过期时间（分钟）
     */
    private static final long DEFAULT_TTL = 30;

    /**
     * 默认过期时间单位
     */
    private static final TimeUnit DEFAULT_TTL_UNIT = TimeUnit.MINUTES;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 保存单个用户角色关系到 Redis
     *
     * @param relation 用户角色关系实体对象
     */
    public void save(UserRoleRelationDO relation) {
        if (relation == null || relation.getId() == null) {
            log.warn("用户角色关系或关系ID为空，无法保存");
            return;
        }
        String key = USER_ROLE_KEY_PREFIX + relation.getId();
        redisTemplate.opsForValue().set(key, relation, DEFAULT_TTL, DEFAULT_TTL_UNIT);
        redisTemplate.opsForSet().add(USER_ROLE_LIST_KEY, relation.getId());

        // 建立用户ID到角色ID的索引
        if (relation.getUserId() != null && relation.getRoleId() != null) {
            redisTemplate.opsForSet().add(USER_ID_ROLES_PREFIX + relation.getUserId(), relation.getRoleId());
            redisTemplate.opsForSet().add(ROLE_ID_USERS_PREFIX + relation.getRoleId(), relation.getUserId());
        }

        log.debug("保存用户角色关系到 Redis: userId={}, roleId={}", relation.getUserId(), relation.getRoleId());
    }

    /**
     * 批量保存用户角色关系到 Redis
     *
     * @param relations 用户角色关系实体列表
     */
    public void saveAll(List<UserRoleRelationDO> relations) {
        if (relations == null || relations.isEmpty()) {
            log.warn("用户角色关系列表为空，跳过批量保存");
            return;
        }
        for (UserRoleRelationDO relation : relations) {
            save(relation);
        }
        log.info("批量保存用户角色关系到 Redis，共 {} 条", relations.size());
    }

    /**
     * 根据关系 ID 从 Redis 获取用户角色关系信息
     *
     * @param id 关系 ID
     * @return 用户角色关系实体对象，如果不存在则返回 null
     */
    public UserRoleRelationDO getById(Long id) {
        if (id == null) {
            log.warn("关系 ID 为空，无法查询");
            return null;
        }
        String key = USER_ROLE_KEY_PREFIX + id;
        Object obj = redisTemplate.opsForValue().get(key);
        if (obj == null) {
            log.debug("关系 ID {} 在 Redis 中不存在", id);
            return null;
        }
        return JSON.parseObject(JSON.toJSONString(obj), UserRoleRelationDO.class);
    }

    /**
     * 从 Redis 获取所有用户角色关系列表
     *
     * @return 所有用户角色关系实体列表
     */
    public List<UserRoleRelationDO> getAll() {
        List<UserRoleRelationDO> relations = new ArrayList<>();
        Set<Object> relationIds = redisTemplate.opsForSet().members(USER_ROLE_LIST_KEY);
        if (relationIds == null || relationIds.isEmpty()) {
            log.debug("Redis 中没有用户角色关系数据");
            return relations;
        }
        for (Object idObj : relationIds) {
            Long id = Long.valueOf(idObj.toString());
            UserRoleRelationDO relation = getById(id);
            if (relation != null) {
                relations.add(relation);
            }
        }
        log.info("从 Redis 获取所有用户角色关系，共 {} 条", relations.size());
        return relations;
    }

    /**
     * 根据用户 ID 获取该用户的所有角色 ID 列表
     *
     * @param userId 用户 ID
     * @return 角色 ID 列表
     */
    public List<Long> getRoleIdsByUserId(Long userId) {
        if (userId == null) {
            return new ArrayList<>();
        }
        Set<Object> roleIds = redisTemplate.opsForSet().members(USER_ID_ROLES_PREFIX + userId);
        if (roleIds == null) {
            return new ArrayList<>();
        }
        return roleIds.stream().map(id -> Long.valueOf(id.toString())).collect(Collectors.toList());
    }

    /**
     * 根据角色 ID 获取拥有该角色的所有用户 ID 列表
     *
     * @param roleId 角色 ID
     * @return 用户 ID 列表
     */
    public List<Long> getUserIdsByRoleId(Long roleId) {
        if (roleId == null) {
            return new ArrayList<>();
        }
        Set<Object> userIds = redisTemplate.opsForSet().members(ROLE_ID_USERS_PREFIX + roleId);
        if (userIds == null) {
            return new ArrayList<>();
        }
        return userIds.stream().map(id -> Long.valueOf(id.toString())).collect(Collectors.toList());
    }

    /**
     * 根据关系 ID 从 Redis 删除用户角色关系
     *
     * @param id 关系 ID
     */
    public void deleteById(Long id) {
        if (id == null) {
            log.warn("关系 ID 为空，无法删除");
            return;
        }
        UserRoleRelationDO relation = getById(id);
        if (relation != null) {
            // 删除索引
            if (relation.getUserId() != null && relation.getRoleId() != null) {
                redisTemplate.opsForSet().remove(USER_ID_ROLES_PREFIX + relation.getUserId(), relation.getRoleId());
                redisTemplate.opsForSet().remove(ROLE_ID_USERS_PREFIX + relation.getRoleId(), relation.getUserId());
            }
        }

        String key = USER_ROLE_KEY_PREFIX + id;
        redisTemplate.delete(key);
        redisTemplate.opsForSet().remove(USER_ROLE_LIST_KEY, id);
        log.debug("从 Redis 删除用户角色关系: id={}", id);
    }

    /**
     * 删除用户的所有角色关系
     *
     * @param userId 用户 ID
     */
    public void deleteByUserId(Long userId) {
        if (userId == null) {
            return;
        }
        List<UserRoleRelationDO> allRelations = getAll();
        for (UserRoleRelationDO relation : allRelations) {
            if (userId.equals(relation.getUserId())) {
                deleteById(relation.getId());
            }
        }
    }

    /**
     * 删除角色的所有用户关系
     *
     * @param roleId 角色 ID
     */
    public void deleteByRoleId(Long roleId) {
        if (roleId == null) {
            return;
        }
        List<UserRoleRelationDO> allRelations = getAll();
        for (UserRoleRelationDO relation : allRelations) {
            if (roleId.equals(relation.getRoleId())) {
                deleteById(relation.getId());
            }
        }
    }

    /**
     * 清空 Redis 中所有用户角色关系数据
     */
    public void clearAll() {
        Set<Object> relationIds = redisTemplate.opsForSet().members(USER_ROLE_LIST_KEY);
        if (relationIds != null && !relationIds.isEmpty()) {
            List<String> keys = new ArrayList<>();
            for (Object idObj : relationIds) {
                keys.add(USER_ROLE_KEY_PREFIX + idObj);
            }
            redisTemplate.delete(keys);
        }

        // 清理所有索引
        Set<String> indexKeys = redisTemplate.keys(USER_ID_ROLES_PREFIX + "*");
        if (indexKeys != null) {
            redisTemplate.delete(indexKeys);
        }
        Set<String> roleIndexKeys = redisTemplate.keys(ROLE_ID_USERS_PREFIX + "*");
        if (roleIndexKeys != null) {
            redisTemplate.delete(roleIndexKeys);
        }

        redisTemplate.delete(USER_ROLE_LIST_KEY);
        log.info("清空 Redis 中所有用户角色关系数据");
    }
}
