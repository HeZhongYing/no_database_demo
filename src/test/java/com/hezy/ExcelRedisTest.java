package com.hezy;

import com.alibaba.excel.EasyExcel;
import com.hezy.dal.redis.UserRedisRepository;
import com.hezy.pojo.UserDO;
import com.hezy.service.impl.ExcelFileHandlerServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Excel + Redis 集成测试
 *
 * @author hezy
 */
@Slf4j
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ExcelRedisTest {

    private static final String TEST_FILE_PATH = "data/test_users.xlsx";

    @Resource
    private ExcelFileHandlerServiceImpl excelFileHandlerService;

    @Resource
    private UserRedisRepository userRedisRepository;

    /**
     * 准备测试数据
     */
    private List<UserDO> createTestUsers() {
        List<UserDO> users = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            UserDO user = new UserDO();
            user.setId((long) i);
            user.setUsername("testuser" + i);
            user.setPassword("password" + i);
            user.setNickname("测试用户" + i);
            user.setUserType(1);
            user.setEmail("test" + i + "@example.com");
            user.setPhone("1390000000" + i);
            user.setStatus(1);
            user.setRemark("这是测试用户" + i);
            user.setCreateBy(1L);
            user.setCreateTime(LocalDateTime.now());
            user.setDeleted(false);
            users.add(user);
        }
        return users;
    }

    /**
     * 测试1: 写入 Excel 文件
     */
    @Test
    @Order(1)
    void test1_WriteExcel() {
        log.info("========== 测试1: 写入 Excel 文件 ==========");

        List<UserDO> users = createTestUsers();

        // 确保目录存在
        File file = new File(TEST_FILE_PATH);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        // 写入 Excel
        excelFileHandlerService.saveData(users, TEST_FILE_PATH, UserDO.class);

        // 验证文件是否存在
        assertTrue(file.exists(), "Excel 文件应该被创建");
        log.info("Excel 文件写入成功: {}", file.getAbsolutePath());
    }

    /**
     * 测试2: 读取 Excel 文件
     */
    @Test
    @Order(2)
    void test2_ReadExcel() {
        log.info("========== 测试2: 读取 Excel 文件 ==========");

        List<UserDO> users = excelFileHandlerService.loadData(TEST_FILE_PATH, UserDO.class);

        assertNotNull(users, "读取的数据不应该为 null");
        assertEquals(3, users.size(), "应该读取到 3 条用户数据");

        for (UserDO user : users) {
            log.info("读取到用户: id={}, username={}, nickname={}",
                    user.getId(), user.getUsername(), user.getNickname());
        }
    }

    /**
     * 测试3: 清空 Redis 数据
     */
    @Test
    @Order(3)
    void test3_ClearRedis() {
        log.info("========== 测试3: 清空 Redis 数据 ==========");
        userRedisRepository.clearAll();
        List<UserDO> users = userRedisRepository.getAll();
        assertTrue(users.isEmpty(), "Redis 应该被清空");
        log.info("Redis 已清空");
    }

    /**
     * 测试4: 保存用户到 Redis
     */
    @Test
    @Order(4)
    void test4_SaveToRedis() {
        log.info("========== 测试4: 保存用户到 Redis ==========");

        List<UserDO> users = createTestUsers();
        userRedisRepository.saveAll(users);

        List<UserDO> savedUsers = userRedisRepository.getAll();
        assertEquals(3, savedUsers.size(), "Redis 中应该有 3 条用户数据");

        for (UserDO user : savedUsers) {
            log.info("Redis 中的用户: id={}, username={}", user.getId(), user.getUsername());
        }
    }

    /**
     * 测试5: 根据 ID 从 Redis 获取用户
     */
    @Test
    @Order(5)
    void test5_GetFromRedisById() {
        log.info("========== 测试5: 根据 ID 从 Redis 获取用户 ==========");

        UserDO user = userRedisRepository.getById(1L);

        assertNotNull(user, "应该能获取到 ID 为 1 的用户");
        assertEquals("testuser1", user.getUsername(), "用户名应该是 testuser1");
        log.info("获取到用户: {}", user);
    }

    /**
     * 测试6: 从 Redis 删除用户
     */
    @Test
    @Order(6)
    void test6_DeleteFromRedis() {
        log.info("========== 测试6: 从 Redis 删除用户 ==========");

        userRedisRepository.deleteById(3L);

        List<UserDO> users = userRedisRepository.getAll();
        assertEquals(2, users.size(), "删除后应该剩下 2 条用户数据");

        UserDO deletedUser = userRedisRepository.getById(3L);
        assertNull(deletedUser, "ID 为 3 的用户应该已被删除");

        log.info("用户删除成功");
    }

    /**
     * 测试7: 完整流程 - Excel -> Redis -> Excel
     */
    @Test
    @Order(7)
    void test7_FullFlow() {
        log.info("========== 测试7: 完整流程 - Excel -> Redis -> Excel ==========");

        // 1. 从 Excel 读取数据
        List<UserDO> usersFromExcel = excelFileHandlerService.loadData(TEST_FILE_PATH, UserDO.class);
        log.info("从 Excel 读取了 {} 条数据", usersFromExcel.size());

        // 2. 清空 Redis 并保存新数据
        userRedisRepository.clearAll();
        userRedisRepository.saveAll(usersFromExcel);

        // 3. 从 Redis 读取数据
        List<UserDO> usersFromRedis = userRedisRepository.getAll();
        log.info("从 Redis 读取了 {} 条数据", usersFromRedis.size());

        // 4. 保存回 Excel（新文件）
        String outputPath = "data/test_users_output.xlsx";
        excelFileHandlerService.saveData(usersFromRedis, outputPath, UserDO.class);

        // 验证
        File outputFile = new File(outputPath);
        assertTrue(outputFile.exists(), "输出文件应该被创建");

        log.info("完整流程测试完成！输出文件: {}", outputFile.getAbsolutePath());
    }

    /**
     * 测试8: 使用 EasyExcel 直接写入（作为对比）
     */
    @Test
    @Order(8)
    void test8_EasyExcelDirectWrite() {
        log.info("========== 测试8: 使用 EasyExcel 直接写入 ==========");

        List<UserDO> users = createTestUsers();
        String directPath = "data/test_easyexcel_direct.xlsx";

        EasyExcel.write(directPath, UserDO.class).sheet("用户").doWrite(users);

        File file = new File(directPath);
        assertTrue(file.exists(), "直接写入的 Excel 文件应该存在");

        log.info("EasyExcel 直接写入成功: {}", file.getAbsolutePath());
    }

    /**
     * 清理测试文件
     */
    @BeforeEach
    void cleanUp() {
        // 每次测试前清理，但保留 test_users.xlsx 供测试使用
    }
}
