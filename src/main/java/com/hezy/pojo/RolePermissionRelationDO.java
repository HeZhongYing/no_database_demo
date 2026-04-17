package com.hezy.pojo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 角色权限关系实体
 *
 * @author hezhongying
 * @version 1.0.0
 * @create 2026/2/4 21:46
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ColumnWidth(15)
public class RolePermissionRelationDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 10005L;

    /**
     * Id
     */
    @ExcelProperty("关系ID")
    private Long id;

    /**
     * 角色Id
     */
    @ExcelProperty("角色ID")
    private Long roleId;

    /**
     * 权限Id
     */
    @ExcelProperty("权限ID")
    private Long permissionId;
}
