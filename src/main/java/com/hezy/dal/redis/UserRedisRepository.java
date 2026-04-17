package com.hezy.dal.redis;

import com.alibaba.fastjson2.JSON;
import com.hezy.pojo.UserDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 用户 Redis 数据访问层
 *
 * @author hezhongying
 */
@Slf4j
@Repository
public class UserRedisRepository {

    private static final String USER_KEY_PREFIX = "user:";
    private static final String USER_LIST_KEY = "user:list";
    private static final long DEFAULT_TTL = 30;
    private static final TimeUnit DEFAULT_TTL_UNIT = TimeUnit.MINUTES;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 保存用户
     */
    public void save(UserDO user) {
        if (user == null || user.getId() == null) {
            log.warn("用户或用户ID为空，无法保存");
            return;
        }
        String key = USER_KEY_PREFIX + user.getId();
        redisTemplate.opsForValue().set(key, user, DEFAULT_TTL, DEFAULT_TTL_UNIT);
        redisTemplate.opsForSet().add(USER_LIST_KEY, user.getId());
        log.debug("保存用户到 Redis: {}", user.getUsername());
    }

    /**
     * 批量保存用户
     */
    public void saveAll(List<UserDO> users) {
        if (users == null || users.isEmpty()) {
            return;
        }
        for (UserDO user : users) {
            save(user);
        }
        log.info("批量保存用户到 Redis，共 {} 条", users.size());
    }

    /**
     * 根据ID获取用户
     */
    public UserDO getById(Long id) {
        if (id == null) {
            return null;
        }
        String key = USER_KEY_PREFIX + id;
        Object obj = redisTemplate.opsForValue().get(key);
        if (obj == null) {
            return null;
        }
        return JSON.parseObject(JSON.toJSONString(obj), UserDO.class);
    }

    /**
     * 获取所有用户
     */
    public List<UserDO> getAll() {
        List<UserDO> users = new ArrayList<>();
        Set<Object> userIds = redisTemplate.opsForSet().members(USER_LIST_KEY);
        if (userIds == null || userIds.isEmpty()) {
            return users;
        }
        for (Object idObj : userIds) {
            Long id = Long.valueOf(idObj.toString());
            UserDO user = getById(id);
            if (user != null) {
                users.add(user);
            }
        }
        log.info("从 Redis 获取所有用户，共 {} 条", users.size());
        return users;
    }

    /**
     * 根据ID删除用户
     */
    public void deleteById(Long id) {
        if (id == null) {
            return;
        }
        String key = USER_KEY_PREFIX + id;
        redisTemplate.delete(key);
        redisTemplate.opsForSet().remove(USER_LIST_KEY, id);
        log.debug("从 Redis 删除用户: id={}", id);
    }

    /**
     * 清空所有用户数据
     */
    public void clearAll() {
        Set<Object> userIds = redisTemplate.opsForSet().members(USER_LIST_KEY);
        if (userIds != null && !userIds.isEmpty()) {
            List<String> keys = new ArrayList<>();
            for (Object idObj : userIds) {
                keys.add(USER_KEY_PREFIX + idObj);
            }
            redisTemplate.delete(keys);
        }
        redisTemplate.delete(USER_LIST_KEY);
        log.info("清空 Redis 中所有用户数据");
    }
}
