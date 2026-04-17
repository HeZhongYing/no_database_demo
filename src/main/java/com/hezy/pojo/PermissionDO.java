package com.hezy.pojo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 权限实体
 *
 * @author hezhongying
 * @version 1.0.0
 * @create 2026/2/4 21:41
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ColumnWidth(15)
public class PermissionDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 10003L;

    /**
     * 权限id
     */
    @ExcelProperty("权限ID")
    private Long id;

    /**
     * 权限名称
     */
    @ExcelProperty("权限名称")
    private String name;

    /**
     * 权限标识
     */
    @ExcelProperty("权限标识")
    private String code;

    /**
     * 权限类型
     */
    @ExcelProperty("权限类型")
    private Integer type;

    /**
     * 权限描述
     */
    @ExcelProperty("权限描述")
    private String description;
}
