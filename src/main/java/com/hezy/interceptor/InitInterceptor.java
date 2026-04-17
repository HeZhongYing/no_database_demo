package com.hezy.interceptor;

import com.hezy.dal.redis.*;
import com.hezy.pojo.*;
import com.hezy.service.impl.ExcelFileHandlerServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;

/**
 * 项目初始化拦截器
 * 作用：在项目启动时，将 Excel 文件中的数据加载到 Redis 缓存中
 *
 * @author hezhongying
 * @version 1.0.0
 * @create 2026/2/4 21:33
 */
@Component
@Slf4j
public class InitInterceptor implements ApplicationListener<ContextRefreshedEvent> {

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
     * Spring 容器刷新完成后触发数据加载
     *
     * @param event 容器刷新事件
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("========== 项目启动，开始从 Excel 文件加载数据 ==========");

        // 按顺序加载数据：先主表，后关系表
        loadUserData();
        loadRoleData();
        loadPermissionData();
        loadUserRoleRelationData();
        loadRolePermissionRelationData();

        log.info("========== 所有数据加载完成 ==========");
    }

    /**
     * 加载用户数据
     * 从 users.xlsx 读取用户数据并保存到 Redis
     */
    private void loadUserData() {
        log.info("--- 开始加载用户数据 ---");
        try {
            File file = new File(userDataFilePath);
            if (!file.exists()) {
                log.warn("用户数据文件不存在: {}，跳过加载用户数据", userDataFilePath);
                return;
            }

            List<UserDO> userList = excelFileHandlerService.loadData(userDataFilePath, UserDO.class);
            if (userList.isEmpty()) {
                log.info("用户数据文件为空，跳过加载");
                return;
            }

            userRedisRepository.clearAll();
            userRedisRepository.saveAll(userList);
            log.info("用户数据加载成功，共 {} 条", userList.size());
        } catch (Exception e) {
            log.error("加载用户数据失败", e);
        }
    }

    /**
     * 加载角色数据
     * 从 roles.xlsx 读取角色数据并保存到 Redis
     */
    private void loadRoleData() {
        log.info("--- 开始加载角色数据 ---");
        try {
            File file = new File(roleDataFilePath);
            if (!file.exists()) {
                log.warn("角色数据文件不存在: {}，跳过加载角色数据", roleDataFilePath);
                return;
            }

            List<RoleDO> roleList = excelFileHandlerService.loadData(roleDataFilePath, RoleDO.class);
            if (roleList.isEmpty()) {
                log.info("角色数据文件为空，跳过加载");
                return;
            }

            roleRedisRepository.clearAll();
            roleRedisRepository.saveAll(roleList);
            log.info("角色数据加载成功，共 {} 条", roleList.size());
        } catch (Exception e) {
            log.error("加载角色数据失败", e);
        }
    }

    /**
     * 加载权限数据
     * 从 permissions.xlsx 读取权限数据并保存到 Redis
     */
    private void loadPermissionData() {
        log.info("--- 开始加载权限数据 ---");
        try {
            File file = new File(permissionDataFilePath);
            if (!file.exists()) {
                log.warn("权限数据文件不存在: {}，跳过加载权限数据", permissionDataFilePath);
                return;
            }

            List<PermissionDO> permissionList = excelFileHandlerService.loadData(permissionDataFilePath, PermissionDO.class);
            if (permissionList.isEmpty()) {
                log.info("权限数据文件为空，跳过加载");
                return;
            }

            permissionRedisRepository.clearAll();
            permissionRedisRepository.saveAll(permissionList);
            log.info("权限数据加载成功，共 {} 条", permissionList.size());
        } catch (Exception e) {
            log.error("加载权限数据失败", e);
        }
    }

    /**
     * 加载用户角色关系数据
     * 从 user_roles.xlsx 读取用户角色关系数据并保存到 Redis
     */
    private void loadUserRoleRelationData() {
        log.info("--- 开始加载用户角色关系数据 ---");
        try {
            File file = new File(userRoleDataFilePath);
            if (!file.exists()) {
                log.warn("用户角色关系数据文件不存在: {}，跳过加载", userRoleDataFilePath);
                return;
            }

            List<UserRoleRelationDO> relationList = excelFileHandlerService.loadData(userRoleDataFilePath, UserRoleRelationDO.class);
            if (relationList.isEmpty()) {
                log.info("用户角色关系数据文件为空，跳过加载");
                return;
            }

            userRoleRelationRedisRepository.clearAll();
            userRoleRelationRedisRepository.saveAll(relationList);
            log.info("用户角色关系数据加载成功，共 {} 条", relationList.size());
        } catch (Exception e) {
            log.error("加载用户角色关系数据失败", e);
        }
    }

    /**
     * 加载角色权限关系数据
     * 从 role_permissions.xlsx 读取角色权限关系数据并保存到 Redis
     */
    private void loadRolePermissionRelationData() {
        log.info("--- 开始加载角色权限关系数据 ---");
        try {
            File file = new File(rolePermissionDataFilePath);
            if (!file.exists()) {
                log.warn("角色权限关系数据文件不存在: {}，跳过加载", rolePermissionDataFilePath);
                return;
            }

            List<RolePermissionRelationDO> relationList = excelFileHandlerService.loadData(rolePermissionDataFilePath, RolePermissionRelationDO.class);
            if (relationList.isEmpty()) {
                log.info("角色权限关系数据文件为空，跳过加载");
                return;
            }

            rolePermissionRelationRedisRepository.clearAll();
            rolePermissionRelationRedisRepository.saveAll(relationList);
            log.info("角色权限关系数据加载成功，共 {} 条", relationList.size());
        } catch (Exception e) {
            log.error("加载角色权限关系数据失败", e);
        }
    }
}
