package com.hezy.pojo;

import lombok.Data;

import java.io.Serial;

/**
 * 角色实体
 *
 * @author hezy
 * @version 1.0.0
 * @create 2026/2/4 21:41
 */
@Data
public class RoleDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 10002L;

    /**
     * 角色id
     */
    private Long id;

    /**
     * 角色名
     */
    private String name;

    /**
     * 角色类型
     */
    private Integer roleType;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 状态
     */
    private Integer status;
}
