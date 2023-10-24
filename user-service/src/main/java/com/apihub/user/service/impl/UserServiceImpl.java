package com.apihub.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.apihub.common.common.ErrorCode;
import com.apihub.common.exception.BusinessException;
import com.apihub.user.config.JwtProperties;
import com.apihub.user.mapper.UserMapper;
import com.apihub.user.model.dto.LoginFormDTO;
import com.apihub.user.model.entity.User;
import com.apihub.user.model.vo.UserVO;
import com.apihub.user.service.UserService;
import com.apihub.user.utils.JwtTool;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.apihub.common.utils.RedisConstants.LOGIN_USER_KEY;
import static com.apihub.common.utils.RedisConstants.LOGIN_USER_TTL;
import static com.apihub.user.utils.UserConstant.BAN_ROLE;
import static com.apihub.user.utils.UserConstant.MD5_SALT;

/**
* @author IKUN
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2023-10-22 23:40:52
*/
@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
implements UserService{

    @Resource
    private UserMapper userMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        synchronized (userAccount.intern()) {
            // 账户不能重复
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", userAccount);
            long count = userMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((MD5_SALT + userPassword).getBytes());
            // 3. 分配 accessKey, secretKey
            String accessKey = DigestUtil.md5Hex(MD5_SALT + userAccount + RandomUtil.randomNumbers(5));
            String secretKey = DigestUtil.md5Hex(MD5_SALT + userAccount + RandomUtil.randomNumbers(8));
            // 4. 插入数据
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            user.setAccessKey(accessKey);
            user.setSecretKey(secretKey);
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return user.getId();
        }
    }

    private final JwtTool jwtTool;

    private final JwtProperties jwtProperties;

    @Override
    public UserVO login(LoginFormDTO loginFormDTO) {
        // 1.数据校验
        String userAccount = loginFormDTO.getUserAccount();
        String userPassword = loginFormDTO.getUserPassword();
        //可以添加密码要求

        // 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((MD5_SALT + userPassword).getBytes());

        // 2. 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }

        if (user.getUserRole().equals(BAN_ROLE)){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "被封禁");
        }

        // 5.生成TOKEN
        String token = jwtTool.createToken(user.getId(), jwtProperties.getTokenTTL());

        // 6.封装VO
        UserVO userVo;
        userVo = BeanUtil.copyProperties(user, UserVO.class);
        userVo.setToken(token);

        // 7.保存用户信息到 redis中
        Map<String, Object> userMap = BeanUtil.beanToMap(userVo, new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreNullValue(true)
                        .setFieldValueEditor((fieldName, fieldValue) -> fieldValue != null ? fieldValue.toString() : ""));

        String tokenKey = LOGIN_USER_KEY + user.getId();
        stringRedisTemplate.opsForHash().putAll(tokenKey,userMap);
        // 7.4.设置token有效期
        stringRedisTemplate.expire(tokenKey, LOGIN_USER_TTL, TimeUnit.MINUTES);

        // 8.返回token+user信息

        return userVo;
    }

    @Override
    public UserVO getLoginUser(HttpServletRequest request,String stringToken) {
        String requestToken = null;
        if (request!=null){
            requestToken = request.getHeader("authorization");
        }
        if (stringToken!=null){
            requestToken = stringToken;
        }

        if (StringUtils.isBlank(requestToken)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "token为空");
        }
        Long userId;
        try{
            userId = jwtTool.parseToken(requestToken);
        }catch (BusinessException e){
            log.info("令牌解析失败!");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "令牌错误");
        }
        String tokenKey = LOGIN_USER_KEY + userId;
        Map<Object, Object> userMap = stringRedisTemplate.opsForHash().entries(tokenKey);
        if (userMap.isEmpty()) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "未登录");
        }
        if (userMap.get("userRole").equals(BAN_ROLE)){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "被封禁");
        }
        //刷新redis存储时间
        stringRedisTemplate.expire(tokenKey, LOGIN_USER_TTL, TimeUnit.MINUTES);

        return BeanUtil.fillBeanWithMap(userMap,new UserVO(),false);
    }
}
