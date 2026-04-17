package com.hezy.controller;

import com.hezy.dal.redis.UserRedisRepository;
import com.hezy.pojo.UserDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户控制器
 *
 * @author hezy
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Resource
    private UserRedisRepository userRedisRepository;

    /**
     * 获取所有用户
     */
    @GetMapping
    public List<UserDO> getAllUsers() {
        return userRedisRepository.getAll();
    }

    /**
     * 根据ID获取用户
     */
    @GetMapping("/{id}")
    public UserDO getUserById(@PathVariable Long id) {
        return userRedisRepository.getById(id);
    }

    /**
     * 新增用户
     */
    @PostMapping
    public String addUser(@RequestBody UserDO user) {
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
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id) {
        userRedisRepository.deleteById(id);
        return "用户删除成功";
    }

    /**
     * 清空所有用户
     */
    @DeleteMapping("/clear")
    public String clearAllUsers() {
        userRedisRepository.clearAll();
        return "所有用户已清空";
    }

    /**
     * 创建示例用户数据
     */
    @PostMapping("/sample")
    public String createSampleData() {
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
