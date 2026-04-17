package com.hezy.dal.redis;

import com.alibaba.fastjson2.JSON;
import com.hezy.pojo.PermissionDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 权限 Redis 数据访问层
 * 提供权限数据的 Redis 增删改查操作
 *
 * @author hezhongying
 * @version 1.0.0
 */
@Slf4j
@Repository
public class PermissionRedisRepository {

    /**
     * 权限数据 Redis Key 前缀
     */
    private static final String PERMISSION_KEY_PREFIX = "permission:";

    /**
     * 权限 ID 列表 Redis Key
     */
    private static final String PERMISSION_LIST_KEY = "permission:list";

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
     * 保存单个权限到 Redis
     *
     * @param permission 权限实体对象
     */
    public void save(PermissionDO permission) {
        if (permission == null || permission.getId() == null) {
            log.warn("权限或权限ID为空，无法保存");
            return;
        }
        String key = PERMISSION_KEY_PREFIX + permission.getId();
        redisTemplate.opsForValue().set(key, permission, DEFAULT_TTL, DEFAULT_TTL_UNIT);
        redisTemplate.opsForSet().add(PERMISSION_LIST_KEY, permission.getId());
        log.debug("保存权限到 Redis: {}", permission.getName());
    }

    /**
     * 批量保存权限到 Redis
     *
     * @param permissions 权限实体列表
     */
    public void saveAll(List<PermissionDO> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            log.warn("权限列表为空，跳过批量保存");
            return;
        }
        for (PermissionDO permission : permissions) {
            save(permission);
        }
        log.info("批量保存权限到 Redis，共 {} 条", permissions.size());
    }

    /**
     * 根据权限 ID 从 Redis 获取权限信息
     *
     * @param id 权限 ID
     * @return 权限实体对象，如果不存在则返回 null
     */
    public PermissionDO getById(Long id) {
        if (id == null) {
            log.warn("权限 ID 为空，无法查询");
            return null;
        }
        String key = PERMISSION_KEY_PREFIX + id;
        Object obj = redisTemplate.opsForValue().get(key);
        if (obj == null) {
            log.debug("权限 ID {} 在 Redis 中不存在", id);
            return null;
        }
        return JSON.parseObject(JSON.toJSONString(obj), PermissionDO.class);
    }

    /**
     * 从 Redis 获取所有权限列表
     *
     * @return 所有权限实体列表
     */
    public List<PermissionDO> getAll() {
        List<PermissionDO> permissions = new ArrayList<>();
        Set<Object> permissionIds = redisTemplate.opsForSet().members(PERMISSION_LIST_KEY);
        if (permissionIds == null || permissionIds.isEmpty()) {
            log.debug("Redis 中没有权限数据");
            return permissions;
        }
        for (Object idObj : permissionIds) {
            Long id = Long.valueOf(idObj.toString());
            PermissionDO permission = getById(id);
            if (permission != null) {
                permissions.add(permission);
            }
        }
        log.info("从 Redis 获取所有权限，共 {} 条", permissions.size());
        return permissions;
    }

    /**
     * 根据权限 ID 从 Redis 删除权限
     *
     * @param id 权限 ID
     */
    public void deleteById(Long id) {
        if (id == null) {
            log.warn("权限 ID 为空，无法删除");
            return;
        }
        String key = PERMISSION_KEY_PREFIX + id;
        redisTemplate.delete(key);
        redisTemplate.opsForSet().remove(PERMISSION_LIST_KEY, id);
        log.debug("从 Redis 删除权限: id={}", id);
    }

    /**
     * 清空 Redis 中所有权限数据
     */
    public void clearAll() {
        Set<Object> permissionIds = redisTemplate.opsForSet().members(PERMISSION_LIST_KEY);
        if (permissionIds != null && !permissionIds.isEmpty()) {
            List<String> keys = new ArrayList<>();
            for (Object idObj : permissionIds) {
                keys.add(PERMISSION_KEY_PREFIX + idObj);
            }
            redisTemplate.delete(keys);
        }
        redisTemplate.delete(PERMISSION_LIST_KEY);
        log.info("清空 Redis 中所有权限数据");
    }
}
