package com.hezy.pojo;

import lombok.Data;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DO父类
 *
 * @author hezy
 * @version 1.0.0
 * @create 2026/2/4 21:50
 */
@Data
public class BaseDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 创建人
     */
    private Long createBy;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新人
     */
    private Long updateBy;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 删除标记
     * true-已删除，false-未删除（默认）
     */
    private Boolean deleted = false;
}
