package com.hezy.dal.redis;

import com.alibaba.fastjson2.JSON;
import com.hezy.pojo.RolePermissionRelationDO;
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
 * 角色权限关系 Redis 数据访问层
 * 提供角色权限关系数据的 Redis 增删改查操作
 *
 * @author hezhongying
 * @version 1.0.0
 */
@Slf4j
@Repository
public class RolePermissionRelationRedisRepository {

    /**
     * 角色权限关系数据 Redis Key 前缀
     */
    private static final String ROLE_PERMISSION_KEY_PREFIX = "role_permission:";

    /**
     * 角色权限关系 ID 列表 Redis Key
     */
    private static final String ROLE_PERMISSION_LIST_KEY = "role_permission:list";

    /**
     * 角色ID -> 权限ID列表的索引 Key 前缀
     */
    private static final String ROLE_ID_PERMISSIONS_PREFIX = "role_id_permissions:";

    /**
     * 权限ID -> 角色ID列表的索引 Key 前缀
     */
    private static final String PERMISSION_ID_ROLES_PREFIX = "permission_id_roles:";

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
     * 保存单个角色权限关系到 Redis
     *
     * @param relation 角色权限关系实体对象
     */
    public void save(RolePermissionRelationDO relation) {
        if (relation == null || relation.getId() == null) {
            log.warn("角色权限关系或关系ID为空，无法保存");
            return;
        }
        String key = ROLE_PERMISSION_KEY_PREFIX + relation.getId();
        redisTemplate.opsForValue().set(key, relation, DEFAULT_TTL, DEFAULT_TTL_UNIT);
        redisTemplate.opsForSet().add(ROLE_PERMISSION_LIST_KEY, relation.getId());

        // 建立角色ID到权限ID的索引
        if (relation.getRoleId() != null && relation.getPermissionId() != null) {
            redisTemplate.opsForSet().add(ROLE_ID_PERMISSIONS_PREFIX + relation.getRoleId(), relation.getPermissionId());
            redisTemplate.opsForSet().add(PERMISSION_ID_ROLES_PREFIX + relation.getPermissionId(), relation.getRoleId());
        }

        log.debug("保存角色权限关系到 Redis: roleId={}, permissionId={}", relation.getRoleId(), relation.getPermissionId());
    }

    /**
     * 批量保存角色权限关系到 Redis
     *
     * @param relations 角色权限关系实体列表
     */
    public void saveAll(List<RolePermissionRelationDO> relations) {
        if (relations == null || relations.isEmpty()) {
            log.warn("角色权限关系列表为空，跳过批量保存");
            return;
        }
        for (RolePermissionRelationDO relation : relations) {
            save(relation);
        }
        log.info("批量保存角色权限关系到 Redis，共 {} 条", relations.size());
    }

    /**
     * 根据关系 ID 从 Redis 获取角色权限关系信息
     *
     * @param id 关系 ID
     * @return 角色权限关系实体对象，如果不存在则返回 null
     */
    public RolePermissionRelationDO getById(Long id) {
        if (id == null) {
            log.warn("关系 ID 为空，无法查询");
            return null;
        }
        String key = ROLE_PERMISSION_KEY_PREFIX + id;
        Object obj = redisTemplate.opsForValue().get(key);
        if (obj == null) {
            log.debug("关系 ID {} 在 Redis 中不存在", id);
            return null;
        }
        return JSON.parseObject(JSON.toJSONString(obj), RolePermissionRelationDO.class);
    }

    /**
     * 从 Redis 获取所有角色权限关系列表
     *
     * @return 所有角色权限关系实体列表
     */
    public List<RolePermissionRelationDO> getAll() {
        List<RolePermissionRelationDO> relations = new ArrayList<>();
        Set<Object> relationIds = redisTemplate.opsForSet().members(ROLE_PERMISSION_LIST_KEY);
        if (relationIds == null || relationIds.isEmpty()) {
            log.debug("Redis 中没有角色权限关系数据");
            return relations;
        }
        for (Object idObj : relationIds) {
            Long id = Long.valueOf(idObj.toString());
            RolePermissionRelationDO relation = getById(id);
            if (relation != null) {
                relations.add(relation);
            }
        }
        log.info("从 Redis 获取所有角色权限关系，共 {} 条", relations.size());
        return relations;
    }

    /**
     * 根据角色 ID 获取该角色的所有权限 ID 列表
     *
     * @param roleId 角色 ID
     * @return 权限 ID 列表
     */
    public List<Long> getPermissionIdsByRoleId(Long roleId) {
        if (roleId == null) {
            return new ArrayList<>();
        }
        Set<Object> permissionIds = redisTemplate.opsForSet().members(ROLE_ID_PERMISSIONS_PREFIX + roleId);
        if (permissionIds == null) {
            return new ArrayList<>();
        }
        return permissionIds.stream().map(id -> Long.valueOf(id.toString())).collect(Collectors.toList());
    }

    /**
     * 根据权限 ID 获取拥有该权限的所有角色 ID 列表
     *
     * @param permissionId 权限 ID
     * @return 角色 ID 列表
     */
    public List<Long> getRoleIdsByPermissionId(Long permissionId) {
        if (permissionId == null) {
            return new ArrayList<>();
        }
        Set<Object> roleIds = redisTemplate.opsForSet().members(PERMISSION_ID_ROLES_PREFIX + permissionId);
        if (roleIds == null) {
            return new ArrayList<>();
        }
        return roleIds.stream().map(id -> Long.valueOf(id.toString())).collect(Collectors.toList());
    }

    /**
     * 根据关系 ID 从 Redis 删除角色权限关系
     *
     * @param id 关系 ID
     */
    public void deleteById(Long id) {
        if (id == null) {
            log.warn("关系 ID 为空，无法删除");
            return;
        }
        RolePermissionRelationDO relation = getById(id);
        if (relation != null) {
            // 删除索引
            if (relation.getRoleId() != null && relation.getPermissionId() != null) {
                redisTemplate.opsForSet().remove(ROLE_ID_PERMISSIONS_PREFIX + relation.getRoleId(), relation.getPermissionId());
                redisTemplate.opsForSet().remove(PERMISSION_ID_ROLES_PREFIX + relation.getPermissionId(), relation.getRoleId());
            }
        }

        String key = ROLE_PERMISSION_KEY_PREFIX + id;
        redisTemplate.delete(key);
        redisTemplate.opsForSet().remove(ROLE_PERMISSION_LIST_KEY, id);
        log.debug("从 Redis 删除角色权限关系: id={}", id);
    }

    /**
     * 删除角色的所有权限关系
     *
     * @param roleId 角色 ID
     */
    public void deleteByRoleId(Long roleId) {
        if (roleId == null) {
            return;
        }
        List<RolePermissionRelationDO> allRelations = getAll();
        for (RolePermissionRelationDO relation : allRelations) {
            if (roleId.equals(relation.getRoleId())) {
                deleteById(relation.getId());
            }
        }
    }

    /**
     * 删除权限的所有角色关系
     *
     * @param permissionId 权限 ID
     */
    public void deleteByPermissionId(Long permissionId) {
        if (permissionId == null) {
            return;
        }
        List<RolePermissionRelationDO> allRelations = getAll();
        for (RolePermissionRelationDO relation : allRelations) {
            if (permissionId.equals(relation.getPermissionId())) {
                deleteById(relation.getId());
            }
        }
    }

    /**
     * 清空 Redis 中所有角色权限关系数据
     */
    public void clearAll() {
        Set<Object> relationIds = redisTemplate.opsForSet().members(ROLE_PERMISSION_LIST_KEY);
        if (relationIds != null && !relationIds.isEmpty()) {
            List<String> keys = new ArrayList<>();
            for (Object idObj : relationIds) {
                keys.add(ROLE_PERMISSION_KEY_PREFIX + idObj);
            }
            redisTemplate.delete(keys);
        }

        // 清理所有索引
        Set<String> roleIndexKeys = redisTemplate.keys(ROLE_ID_PERMISSIONS_PREFIX + "*");
        if (roleIndexKeys != null) {
            redisTemplate.delete(roleIndexKeys);
        }
        Set<String> permIndexKeys = redisTemplate.keys(PERMISSION_ID_ROLES_PREFIX + "*");
        if (permIndexKeys != null) {
            redisTemplate.delete(permIndexKeys);
        }

        redisTemplate.delete(ROLE_PERMISSION_LIST_KEY);
        log.info("清空 Redis 中所有角色权限关系数据");
    }
}
