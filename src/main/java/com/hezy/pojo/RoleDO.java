package com.hezy.pojo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 角色实体
 *
 * @author hezhongying
 * @version 1.0.0
 * @create 2026/2/4 21:41
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ColumnWidth(15)
public class RoleDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 10002L;

    /**
     * 角色id
     */
    @ExcelProperty("角色ID")
    private Long id;

    /**
     * 角色名
     */
    @ExcelProperty("角色名称")
    private String name;

    /**
     * 角色类型
     */
    @ExcelProperty("角色类型")
    private Integer roleType;

    /**
     * 角色描述
     */
    @ExcelProperty("角色描述")
    private String description;

    /**
     * 状态
     */
    @ExcelProperty("状态")
    private Integer status;
}
