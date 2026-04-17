package com.hezy.dal.redis;

import com.alibaba.fastjson2.JSON;
import com.hezy.pojo.RoleDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 角色 Redis 数据访问层
 * 提供角色数据的 Redis 增删改查操作
 *
 * @author hezhongying
 * @version 1.0.0
 */
@Slf4j
@Repository
public class RoleRedisRepository {

    /**
     * 角色数据 Redis Key 前缀
     */
    private static final String ROLE_KEY_PREFIX = "role:";

    /**
     * 角色 ID 列表 Redis Key
     */
    private static final String ROLE_LIST_KEY = "role:list";

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
     * 保存单个角色到 Redis
     *
     * @param role 角色实体对象
     */
    public void save(RoleDO role) {
        if (role == null || role.getId() == null) {
            log.warn("角色或角色ID为空，无法保存");
            return;
        }
        String key = ROLE_KEY_PREFIX + role.getId();
        redisTemplate.opsForValue().set(key, role, DEFAULT_TTL, DEFAULT_TTL_UNIT);
        redisTemplate.opsForSet().add(ROLE_LIST_KEY, role.getId());
        log.debug("保存角色到 Redis: {}", role.getName());
    }

    /**
     * 批量保存角色到 Redis
     *
     * @param roles 角色实体列表
     */
    public void saveAll(List<RoleDO> roles) {
        if (roles == null || roles.isEmpty()) {
            log.warn("角色列表为空，跳过批量保存");
            return;
        }
        for (RoleDO role : roles) {
            save(role);
        }
        log.info("批量保存角色到 Redis，共 {} 条", roles.size());
    }

    /**
     * 根据角色 ID 从 Redis 获取角色信息
     *
     * @param id 角色 ID
     * @return 角色实体对象，如果不存在则返回 null
     */
    public RoleDO getById(Long id) {
        if (id == null) {
            log.warn("角色 ID 为空，无法查询");
            return null;
        }
        String key = ROLE_KEY_PREFIX + id;
        Object obj = redisTemplate.opsForValue().get(key);
        if (obj == null) {
            log.debug("角色 ID {} 在 Redis 中不存在", id);
            return null;
        }
        return JSON.parseObject(JSON.toJSONString(obj), RoleDO.class);
    }

    /**
     * 从 Redis 获取所有角色列表
     *
     * @return 所有角色实体列表
     */
    public List<RoleDO> getAll() {
        List<RoleDO> roles = new ArrayList<>();
        Set<Object> roleIds = redisTemplate.opsForSet().members(ROLE_LIST_KEY);
        if (roleIds == null || roleIds.isEmpty()) {
            log.debug("Redis 中没有角色数据");
            return roles;
        }
        for (Object idObj : roleIds) {
            Long id = Long.valueOf(idObj.toString());
            RoleDO role = getById(id);
            if (role != null) {
                roles.add(role);
            }
        }
        log.info("从 Redis 获取所有角色，共 {} 条", roles.size());
        return roles;
    }

    /**
     * 根据角色 ID 从 Redis 删除角色
     *
     * @param id 角色 ID
     */
    public void deleteById(Long id) {
        if (id == null) {
            log.warn("角色 ID 为空，无法删除");
            return;
        }
        String key = ROLE_KEY_PREFIX + id;
        redisTemplate.delete(key);
        redisTemplate.opsForSet().remove(ROLE_LIST_KEY, id);
        log.debug("从 Redis 删除角色: id={}", id);
    }

    /**
     * 清空 Redis 中所有角色数据
     */
    public void clearAll() {
        Set<Object> roleIds = redisTemplate.opsForSet().members(ROLE_LIST_KEY);
        if (roleIds != null && !roleIds.isEmpty()) {
            List<String> keys = new ArrayList<>();
            for (Object idObj : roleIds) {
                keys.add(ROLE_KEY_PREFIX + idObj);
            }
            redisTemplate.delete(keys);
        }
        redisTemplate.delete(ROLE_LIST_KEY);
        log.info("清空 Redis 中所有角色数据");
    }
}
