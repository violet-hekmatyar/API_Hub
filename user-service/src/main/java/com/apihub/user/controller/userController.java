package com.apihub.user.controller;


import com.apihub.common.common.BaseResponse;
import com.apihub.common.common.ErrorCode;
import com.apihub.common.common.ResultUtils;
import com.apihub.common.exception.BusinessException;
import com.apihub.user.annotation.AuthCheck;
import com.apihub.user.model.dto.LoginFormDTO;
import com.apihub.user.model.dto.UserAddRequest;
import com.apihub.user.model.dto.UserQueryRequest;
import com.apihub.user.model.dto.UserRegisterRequest;
import com.apihub.user.model.entity.User;
import com.apihub.user.model.vo.UserVO;
import com.apihub.user.service.UserService;
import com.apihub.user.utils.UserConstant;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

import static com.apihub.common.common.ErrorCode.NOT_LOGIN_ERROR;
import static com.apihub.common.common.ErrorCode.PARAMS_ERROR;

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

    @ApiOperation("获取当前登录用户接口")
    @GetMapping("/get/login")
    public BaseResponse<UserVO> getLoginUser(HttpServletRequest request) {
        UserVO user ;
        user = userService.getLoginUser(request);
        if (user==null){
            return new BaseResponse<>(NOT_LOGIN_ERROR);
        }
        return ResultUtils.success(user);
    }

    @ApiOperation("添加用户接口")
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest, HttpServletRequest request) {
        if (userAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount =userAddRequest.getUserAccount();
        String userPassword = userAddRequest.getUserPassword();
        String checkPassword = userPassword;
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return new BaseResponse(PARAMS_ERROR);
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    @ApiOperation("根据id获取用户接口")
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<UserVO> getUserById(int id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getById(id);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return ResultUtils.success(userVO);
    }

    @ApiOperation("全局搜索用户接口")
    @GetMapping("/list")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<List<UserVO>> listUser(@RequestBody UserQueryRequest userQueryRequest, HttpServletRequest request) {
        User userQuery = new User();
        if (userQueryRequest != null) {
            BeanUtils.copyProperties(userQueryRequest, userQuery);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>(userQuery);
        List<User> userList = userService.list(queryWrapper);
        List<UserVO> userVOList = userList.stream().map(user -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            return userVO;
        }).collect(Collectors.toList());
        return ResultUtils.success(userVOList);
    }

    @GetMapping("/list/page")
    public BaseResponse<Page<UserVO>> listUserByPage(@RequestBody UserQueryRequest userQueryRequest, HttpServletRequest request) {

        //检查是否登录
        getLoginUser(request);

        long current = 1;
        long size = 10;
        User userQuery = new User();
        if (userQueryRequest != null) {
            BeanUtils.copyProperties(userQueryRequest, userQuery);
            current = userQueryRequest.getCurrent();
            size = userQueryRequest.getPageSize();
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>(userQuery);
        Page<User> userPage = userService.page(new Page<>(current, size), queryWrapper);
        Page<UserVO> userVOPage = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        List<UserVO> userVOList = userPage.getRecords().stream().map(user -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            return userVO;
        }).collect(Collectors.toList());
        userVOPage.setRecords(userVOList);
        return ResultUtils.success(userVOPage);
    }


}
