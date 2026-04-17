package com.hezy;

import com.alibaba.excel.EasyExcel;
import com.hezy.pojo.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Excel 测试数据生成工具类
 * 用于在 data 目录下生成初始的 Excel 测试数据文件
 *
 * @author hezhongying
 * @version 2.0.0
 */
@Slf4j
public class ExcelDataGenerator {

    /**
     * 数据文件存放目录
     */
    private static final String DATA_DIR = "data/";

    /**
     * 测试数据量配置
     */
    private static final int USER_COUNT = 100000;      // 10万用户
    private static final int ROLE_COUNT = 100;          // 100个角色
    private static final int PERMISSION_COUNT = 500;    // 500个权限
    private static final int USER_ROLE_COUNT = 150000;  // 15万用户角色关系
    private static final int ROLE_PERMISSION_COUNT = 200000; // 20万角色权限关系

    private static final Random RANDOM = new Random();

    /**
     * 主方法：生成所有测试数据 Excel 文件
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        log.info("========== 开始生成 Excel 测试数据文件（大数据量版）==========");
        log.info("配置数据量：用户={}, 角色={}, 权限={}, 用户角色关系={}, 角色权限关系={}",
                USER_COUNT, ROLE_COUNT, PERMISSION_COUNT, USER_ROLE_COUNT, ROLE_PERMISSION_COUNT);

        long startTime = System.currentTimeMillis();

        try {
            // 确保目录存在
            java.io.File dir = new java.io.File(DATA_DIR);
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                if (created) {
                    log.info("创建数据目录: {}", dir.getAbsolutePath());
                }
            }

            // 生成权限数据（先生成权限，因为角色需要权限）
            generatePermissionData();

            // 生成角色数据
            generateRoleData();

            // 生成用户数据
            generateUserData();

            // 生成用户角色关系数据
            generateUserRoleRelationData();

            // 生成角色权限关系数据
            generateRolePermissionRelationData();

            long endTime = System.currentTimeMillis();
            log.info("========== Excel 测试数据文件生成完成 ==========");
            log.info("总耗时: {} 秒", (endTime - startTime) / 1000.0);
            log.info("文件位置: {}", new java.io.File(DATA_DIR).getAbsolutePath());
        } catch (Exception e) {
            log.error("生成 Excel 测试数据文件失败", e);
        }
    }

    /**
     * 生成用户数据 Excel 文件（10万条）
     */
    private static void generateUserData() {
        log.info("开始生成用户数据文件，目标: {} 条...", USER_COUNT);
        long startTime = System.currentTimeMillis();

        List<UserDO> users = new ArrayList<>();
        String[] surnames = {"张", "李", "王", "刘", "陈", "杨", "黄", "赵", "周", "吴", "徐", "孙", "马", "朱", "胡", "郭", "何", "林", "罗", "高"};
        String[] names = {"伟", "芳", "娜", "秀英", "敏", "静", "丽", "强", "磊", "洋", "艳", "勇", "军", "杰", "娟", "涛", "明", "超", "秀兰", "霞"};

        for (long i = 1; i <= USER_COUNT; i++) {
            UserDO user = new UserDO();
            user.setId(i);

            String surname = surnames[RANDOM.nextInt(surnames.length)];
            String name = names[RANDOM.nextInt(names.length)];
            String nickname = surname + name + (i > 20 ? i : "");

            user.setUsername("user" + i);
            user.setPassword(UUID.randomUUID().toString().substring(0, 8));
            user.setNickname(nickname);
            user.setUserType(RANDOM.nextInt(3) + 1);
            user.setEmail("user" + i + "@example.com");
            user.setPhone("1" + (3 + RANDOM.nextInt(6)) + String.format("%09d", i % 1000000000));
            user.setStatus(RANDOM.nextInt(2) + 1); // 1或2
            user.setRemark("测试用户" + i);
            user.setCreateBy(1L);
            user.setCreateTime(LocalDateTime.now().minusDays(RANDOM.nextInt(365)));
            user.setDeleted(false);

            users.add(user);

            // 每1万条输出一次进度
            if (i % 10000 == 0) {
                log.info("用户数据生成进度: {}/{}", i, USER_COUNT);
            }
        }

        String fileName = DATA_DIR + "users.xlsx";
        EasyExcel.write(fileName, UserDO.class).sheet("用户列表").doWrite(users);

        long endTime = System.currentTimeMillis();
        log.info("用户数据文件已生成: {}, 共 {} 条数据, 耗时: {}s",
                fileName, users.size(), (endTime - startTime) / 1000.0);
    }

    /**
     * 生成角色数据 Excel 文件（100条）
     */
    private static void generateRoleData() {
        log.info("开始生成角色数据文件，目标: {} 条...", ROLE_COUNT);
        long startTime = System.currentTimeMillis();

        List<RoleDO> roles = new ArrayList<>();
        String[] roleTypes = {"管理员", "操作员", "查看员", "审核员", "系统", "业务", "财务", "人事", "市场", "技术"};
        String[] roleLevels = {"一级", "二级", "三级", "四级", "五级", "六级", "七级", "八级", "九级", "十级"};

        for (long i = 1; i <= ROLE_COUNT; i++) {
            RoleDO role = new RoleDO();
            role.setId(i);

            String type = roleTypes[RANDOM.nextInt(roleTypes.length)];
            String level = roleLevels[RANDOM.nextInt(roleLevels.length)];

            role.setName(level + type + "角色" + i);
            role.setRoleType(RANDOM.nextInt(5) + 1);
            role.setDescription("这是" + level + type + "角色的描述信息，角色编号" + i);
            role.setStatus(RANDOM.nextInt(2) + 1);
            role.setCreateBy(1L);
            role.setCreateTime(LocalDateTime.now().minusDays(RANDOM.nextInt(365)));
            role.setDeleted(false);

            roles.add(role);
        }

        String fileName = DATA_DIR + "roles.xlsx";
        EasyExcel.write(fileName, RoleDO.class).sheet("角色列表").doWrite(roles);

        long endTime = System.currentTimeMillis();
        log.info("角色数据文件已生成: {}, 共 {} 条数据, 耗时: {}s",
                fileName, roles.size(), (endTime - startTime) / 1000.0);
    }

    /**
     * 生成权限数据 Excel 文件（500条）
     */
    private static void generatePermissionData() {
        log.info("开始生成权限数据文件，目标: {} 条...", PERMISSION_COUNT);
        long startTime = System.currentTimeMillis();

        List<PermissionDO> permissions = new ArrayList<>();
        String[] permModules = {"用户", "角色", "权限", "部门", "项目", "任务", "文档", "报表", "系统", "配置"};
        String[] permActions = {"查看", "创建", "编辑", "删除", "审核", "发布", "导出", "导入", "分配", "配置"};

        for (long i = 1; i <= PERMISSION_COUNT; i++) {
            PermissionDO permission = new PermissionDO();
            permission.setId(i);

            String module = permModules[RANDOM.nextInt(permModules.length)];
            String action = permActions[RANDOM.nextInt(permActions.length)];

            permission.setName(module + action);
            permission.setCode(module.toLowerCase() + ":" + action.toLowerCase() + ":" + i);
            permission.setType(RANDOM.nextInt(10) + 1);
            permission.setDescription(module + "模块的" + action + "权限，权限编号" + i);
            permission.setCreateBy(1L);
            permission.setCreateTime(LocalDateTime.now().minusDays(RANDOM.nextInt(365)));
            permission.setDeleted(false);

            permissions.add(permission);
        }

        String fileName = DATA_DIR + "permissions.xlsx";
        EasyExcel.write(fileName, PermissionDO.class).sheet("权限列表").doWrite(permissions);

        long endTime = System.currentTimeMillis();
        log.info("权限数据文件已生成: {}, 共 {} 条数据, 耗时: {}s",
                fileName, permissions.size(), (endTime - startTime) / 1000.0);
    }

    /**
     * 生成用户角色关系数据 Excel 文件（15万条）
     */
    private static void generateUserRoleRelationData() {
        log.info("开始生成用户角色关系数据文件，目标: {} 条...", USER_ROLE_COUNT);
        long startTime = System.currentTimeMillis();

        List<UserRoleRelationDO> relations = new ArrayList<>();
        long relationId = 1;

        // 每个用户至少分配一个角色
        for (long userId = 1; userId <= USER_COUNT; userId++) {
            // 每个用户分配1-3个角色
            int roleCount = RANDOM.nextInt(3) + 1;
            for (int j = 0; j < roleCount && relationId <= USER_ROLE_COUNT; j++) {
                long roleId = RANDOM.nextInt(ROLE_COUNT) + 1;

                UserRoleRelationDO relation = new UserRoleRelationDO();
                relation.setId(relationId++);
                relation.setUserId(userId);
                relation.setRoleId(roleId);
                relation.setCreateTime(LocalDateTime.now().minusDays(RANDOM.nextInt(365)));
                relation.setDeleted(false);

                relations.add(relation);

                // 每1万条输出一次进度
                if ((relationId - 1) % 10000 == 0) {
                    log.info("用户角色关系数据生成进度: {}/{}", (relationId - 1), USER_ROLE_COUNT);
                }
            }

            if (relationId > USER_ROLE_COUNT) {
                break;
            }
        }

        String fileName = DATA_DIR + "user_roles.xlsx";
        EasyExcel.write(fileName, UserRoleRelationDO.class).sheet("用户角色关系").doWrite(relations);

        long endTime = System.currentTimeMillis();
        log.info("用户角色关系数据文件已生成: {}, 共 {} 条数据, 耗时: {}s",
                fileName, relations.size(), (endTime - startTime) / 1000.0);
    }

    /**
     * 生成角色权限关系数据 Excel 文件（20万条）
     */
    private static void generateRolePermissionRelationData() {
        log.info("开始生成角色权限关系数据文件，目标: {} 条...", ROLE_PERMISSION_COUNT);
        long startTime = System.currentTimeMillis();

        List<RolePermissionRelationDO> relations = new ArrayList<>();
        long relationId = 1;

        // 每个角色至少分配一个权限
        for (long roleId = 1; roleId <= ROLE_COUNT; roleId++) {
            // 每个角色分配5-50个权限
            int permCount = RANDOM.nextInt(46) + 5;
            for (int j = 0; j < permCount && relationId <= ROLE_PERMISSION_COUNT; j++) {
                long permId = RANDOM.nextInt(PERMISSION_COUNT) + 1;

                RolePermissionRelationDO relation = new RolePermissionRelationDO();
                relation.setId(relationId++);
                relation.setRoleId(roleId);
                relation.setPermissionId(permId);
                relation.setCreateTime(LocalDateTime.now().minusDays(RANDOM.nextInt(365)));
                relation.setDeleted(false);

                relations.add(relation);

                // 每1万条输出一次进度
                if ((relationId - 1) % 10000 == 0) {
                    log.info("角色权限关系数据生成进度: {}/{}", (relationId - 1), ROLE_PERMISSION_COUNT);
                }
            }

            if (relationId > ROLE_PERMISSION_COUNT) {
                break;
            }
        }

        String fileName = DATA_DIR + "role_permissions.xlsx";
        EasyExcel.write(fileName, RolePermissionRelationDO.class).sheet("角色权限关系").doWrite(relations);

        long endTime = System.currentTimeMillis();
        log.info("角色权限关系数据文件已生成: {}, 共 {} 条数据, 耗时: {}s",
                fileName, relations.size(), (endTime - startTime) / 1000.0);
    }
}
