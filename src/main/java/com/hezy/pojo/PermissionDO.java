package com.hezy.pojo;

import lombok.Data;

import java.io.Serial;

/**
 * 权限实体
 *
 * @author hezy
 * @version 1.0.0
 * @create 2026/2/4 21:41
 */
@Data
public class PermissionDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 10003L;

    /**
     * 权限id
     */
    private Long id;

    /**
     * 权限名称
     */
    private String name;

    /**
     * 权限标识
     */
    private String code;

    /**
     * 权限类型
     */
    private Integer type;

    /**
     * 权限描述
     */
    private String description;
}
