package com.apihub.user.controller;


import com.apihub.common.common.BaseResponse;
import com.apihub.common.common.ErrorCode;
import com.apihub.common.common.ResultUtils;
import com.apihub.common.exception.BusinessException;
import com.apihub.user.model.dto.LoginFormDTO;
import com.apihub.user.model.dto.UserRegisterRequest;
import com.apihub.user.model.entity.User;
import com.apihub.user.model.vo.UserVO;
import com.apihub.user.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import static com.apihub.common.common.ErrorCode.NOT_LOGIN_ERROR;

@RestController
@Slf4j
@RequestMapping("/user")
public class userController {
    @Resource
    private UserService userService;

    @ApiOperation("用户注册接口")
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return null;
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    @ApiOperation("用户登录接口")
    @PostMapping("login")
    public UserVO login(@RequestBody LoginFormDTO loginFormDTO){
        if (loginFormDTO == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = loginFormDTO.getUserAccount();
        String userPassword = loginFormDTO.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return userService.login(loginFormDTO);
    }

    @ApiOperation("获取当前用户接口")
    @GetMapping("/get/login")
    public BaseResponse<UserVO> getLoginUser(HttpServletRequest request) {
        User user = new User();
        try{
             user = userService.getLoginUser(request);
        }catch (BusinessException e){
            log.info("令牌解析失败!");
            return new BaseResponse<>(NOT_LOGIN_ERROR);
        }

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return ResultUtils.success(userVO);
    }
}
