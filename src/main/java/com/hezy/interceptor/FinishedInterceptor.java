package com.hezy.interceptor;

import com.hezy.dal.redis.*;
import com.hezy.pojo.*;
import com.hezy.service.impl.ExcelFileHandlerServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;

/**
 * 停机拦截器
 * 作用：在项目停止时，将 Redis 中的数据持久化到 Excel 文件中
 *
 * @author hezhongying
 * @version 1.0.0
 * @create 2026/2/4 21:36
 */
@Component
@Slf4j
public class FinishedInterceptor implements ApplicationListener<ContextClosedEvent> {

    /**
     * 用户数据文件路径
     */
    @Value("${app.data.user-path:data/users.xlsx}")
    private String userDataFilePath;

    /**
     * 角色数据文件路径
     */
    @Value("${app.data.role-path:data/roles.xlsx}")
    private String roleDataFilePath;

    /**
     * 权限数据文件路径
     */
    @Value("${app.data.permission-path:data/permissions.xlsx}")
    private String permissionDataFilePath;

    /**
     * 用户角色关系数据文件路径
     */
    @Value("${app.data.user-role-path:data/user_roles.xlsx}")
    private String userRoleDataFilePath;

    /**
     * 角色权限关系数据文件路径
     */
    @Value("${app.data.role-permission-path:data/role_permissions.xlsx}")
    private String rolePermissionDataFilePath;

    @Resource
    private ExcelFileHandlerServiceImpl excelFileHandlerService;

    @Resource
    private UserRedisRepository userRedisRepository;

    @Resource
    private RoleRedisRepository roleRedisRepository;

    @Resource
    private PermissionRedisRepository permissionRedisRepository;

    @Resource
    private UserRoleRelationRedisRepository userRoleRelationRedisRepository;

    @Resource
    private RolePermissionRelationRedisRepository rolePermissionRelationRedisRepository;

    /**
     * Spring 容器关闭时触发数据持久化
     *
     * @param event 容器关闭事件
     */
    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        log.info("========== 项目停止，开始将数据保存到 Excel 文件 ==========");

        // 按顺序保存数据：先主表，后关系表
        saveUserData();
        saveRoleData();
        savePermissionData();
        saveUserRoleRelationData();
        saveRolePermissionRelationData();

        log.info("========== 所有数据保存完成 ==========");
    }

    /**
     * 保存用户数据到 Excel
     * 从 Redis 读取所有用户数据并保存到 users.xlsx
     */
    private void saveUserData() {
        log.info("--- 开始保存用户数据 ---");
        try {
            List<UserDO> userList = userRedisRepository.getAll();
            if (userList.isEmpty()) {
                log.info("Redis 中没有用户数据，跳过保存");
                return;
            }

            ensureDirectoryExists(userDataFilePath);
            excelFileHandlerService.saveData(userList, userDataFilePath, UserDO.class);
            log.info("用户数据保存成功，共 {} 条", userList.size());
        } catch (Exception e) {
            log.error("保存用户数据失败", e);
        }
    }

    /**
     * 保存角色数据到 Excel
     * 从 Redis 读取所有角色数据并保存到 roles.xlsx
     */
    private void saveRoleData() {
        log.info("--- 开始保存角色数据 ---");
        try {
            List<RoleDO> roleList = roleRedisRepository.getAll();
            if (roleList.isEmpty()) {
                log.info("Redis 中没有角色数据，跳过保存");
                return;
            }

            ensureDirectoryExists(roleDataFilePath);
            excelFileHandlerService.saveData(roleList, roleDataFilePath, RoleDO.class);
            log.info("角色数据保存成功，共 {} 条", roleList.size());
        } catch (Exception e) {
            log.error("保存角色数据失败", e);
        }
    }

    /**
     * 保存权限数据到 Excel
     * 从 Redis 读取所有权限数据并保存到 permissions.xlsx
     */
    private void savePermissionData() {
        log.info("--- 开始保存权限数据 ---");
        try {
            List<PermissionDO> permissionList = permissionRedisRepository.getAll();
            if (permissionList.isEmpty()) {
                log.info("Redis 中没有权限数据，跳过保存");
                return;
            }

            ensureDirectoryExists(permissionDataFilePath);
            excelFileHandlerService.saveData(permissionList, permissionDataFilePath, PermissionDO.class);
            log.info("权限数据保存成功，共 {} 条", permissionList.size());
        } catch (Exception e) {
            log.error("保存权限数据失败", e);
        }
    }

    /**
     * 保存用户角色关系数据到 Excel
     * 从 Redis 读取所有用户角色关系数据并保存到 user_roles.xlsx
     */
    private void saveUserRoleRelationData() {
        log.info("--- 开始保存用户角色关系数据 ---");
        try {
            List<UserRoleRelationDO> relationList = userRoleRelationRedisRepository.getAll();
            if (relationList.isEmpty()) {
                log.info("Redis 中没有用户角色关系数据，跳过保存");
                return;
            }

            ensureDirectoryExists(userRoleDataFilePath);
            excelFileHandlerService.saveData(relationList, userRoleDataFilePath, UserRoleRelationDO.class);
            log.info("用户角色关系数据保存成功，共 {} 条", relationList.size());
        } catch (Exception e) {
            log.error("保存用户角色关系数据失败", e);
        }
    }

    /**
     * 保存角色权限关系数据到 Excel
     * 从 Redis 读取所有角色权限关系数据并保存到 role_permissions.xlsx
     */
    private void saveRolePermissionRelationData() {
        log.info("--- 开始保存角色权限关系数据 ---");
        try {
            List<RolePermissionRelationDO> relationList = rolePermissionRelationRedisRepository.getAll();
            if (relationList.isEmpty()) {
                log.info("Redis 中没有角色权限关系数据，跳过保存");
                return;
            }

            ensureDirectoryExists(rolePermissionDataFilePath);
            excelFileHandlerService.saveData(relationList, rolePermissionDataFilePath, RolePermissionRelationDO.class);
            log.info("角色权限关系数据保存成功，共 {} 条", relationList.size());
        } catch (Exception e) {
            log.error("保存角色权限关系数据失败", e);
        }
    }

    /**
     * 确保文件所在目录存在
     *
     * @param filePath 文件路径
     */
    private void ensureDirectoryExists(String filePath) {
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            boolean created = parentDir.mkdirs();
            if (created) {
                log.info("创建数据目录: {}", parentDir.getAbsolutePath());
            }
        }
    }
}
