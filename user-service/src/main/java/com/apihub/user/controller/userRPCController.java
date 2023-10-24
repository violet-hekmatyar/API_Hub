package com.apihub.user.controller;


import com.apihub.user.model.vo.UserVO;
import com.apihub.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = "userRPC接口")
@RestController
@Slf4j
@RequestMapping("/userRPC")
public class userRPCController {

    @Resource
    private UserService userService;

    @ApiOperation("获取当前登录用户")
    @PostMapping ("/get/login")
    public UserVO getLoginUser(String token) {
        UserVO user;
        user = userService.getLoginUser(null,token);
        return user;
    }
}
