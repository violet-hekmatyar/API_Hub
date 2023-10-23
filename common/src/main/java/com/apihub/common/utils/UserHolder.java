package com.apihub.common.utils;

import com.apihub.common.model.vo.UserVO;

public class UserHolder {
    private static final ThreadLocal<UserVO> tl = new ThreadLocal<>();

    /**
     * 获取当前登录用户信息
     * @return 用户id
     */
    public static UserVO getUser() {
        return tl.get();
    }

    

    /**
     * 保存当前登录用户信息到ThreadLocal
     */
    public static void saveUser(UserVO user){
        tl.set(user);
    }

    /**
     * 移除当前登录用户信息
     */
    public static void removeUser(){
        tl.remove();
    }
}
