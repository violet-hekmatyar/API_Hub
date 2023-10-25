package com.apihub.user.controller;


import com.apihub.user.model.vo.UserVO;
import com.apihub.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Objects;

import static com.apihub.user.utils.UserConstant.ADMIN_ROLE;

@Api(tags = "userRPC接口")
@RestController
@Slf4j
@RequestMapping("/userRPC")
public class userRPCController {

    @Resource
    private UserService userService;

    @ApiOperation("获取当前登录用户")
    @GetMapping("/get/login")
    public UserVO getLoginUser() {
        UserVO user;
        user = userService.getLoginUser();
        return user;
    }

    @ApiOperation("获取当前登录用户")
    @GetMapping("/checkAdmin")
    public Boolean checkAdmin() {
        UserVO user;
        user = userService.getLoginUser();
        return Objects.equals(user.getUserRole(), ADMIN_ROLE);
    }
}
