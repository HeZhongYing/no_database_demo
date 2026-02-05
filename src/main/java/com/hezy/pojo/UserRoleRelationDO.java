package com.hezy.pojo;

import lombok.Data;

import java.io.Serial;


/**
 * 用户角色关系实体
 *
 * @author hezy
 * @version 1.0.0
 * @create 2026/2/4 21:46
 */
@Data
public class UserRoleRelationDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 10004L;

    /**
     * Id
     */
    private Long id;

    /**
     * 用户Id
     */
    private Long userId;

    /**
     * 角色Id
     */
    private Long roleId;
}
