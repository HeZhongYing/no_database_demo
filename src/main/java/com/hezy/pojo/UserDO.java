package com.hezy.pojo;

import lombok.Data;

import java.io.Serial;

/**
 * 用户实体
 *
 * @author hezy
 * @version 1.0.0
 * @create 2026/2/4 21:40
 */
@Data
public class UserDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 10001L;

    /**
     * 用户id
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 用户类型
     */
    private Integer userType;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}
