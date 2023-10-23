package com.apihub.user.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

import static com.apihub.user.utils.UserConstant.DEFAULT_ROLE;

/**
 * 用户创建请求
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Data
public class UserAddRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 账号
     */
    @ApiModelProperty(value = "用户名", required = true)
    private String userAccount;
    /**
    * 密码
    * */
    @ApiModelProperty(value = "密码", required = false)
    private String userPassword = "123456aa";
    /**
     * 用户昵称
     */
    @ApiModelProperty(value = "用户昵称", required = false)
    private String userName;
    /**
     * 用户头像
     */
    @ApiModelProperty(value = "用户头像", required = false)
    private String userAvatar;
    /**
     * 用户角色: user, admin
     */
    @ApiModelProperty(value = "用户权限", required = false)
    private String userRole = DEFAULT_ROLE;
}