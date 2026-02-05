package com.hezy.pojo;

import lombok.Data;

import java.io.Serial;

/**
 * 角色权限关系实体
 *
 * @author hezy
 * @version 1.0.0
 * @create 2026/2/4 21:46
 */
@Data
public class RolePermissionRelationDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 10005L;

    /**
     * Id
     */
    private Long id;

    /**
     * 角色Id
     */
    private Long roleId;

    /**
     * 权限Id
     */
    private Long permissionId;
}
