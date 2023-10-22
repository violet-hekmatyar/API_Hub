package com.apihub.user.service;

import com.apihub.user.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author IKUN
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2023-10-22 23:40:52
*/
public interface UserService extends IService<User> {

    long userRegister(String userAccount, String userPassword, String checkPassword);
}
