package com.apihub.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.apihub.common.common.ErrorCode;
import com.apihub.common.exception.BusinessException;
import com.apihub.common.utils.UserHolder;
import com.apihub.user.config.JwtProperties;
import com.apihub.user.mapper.UserMapper;
import com.apihub.user.model.dto.LoginFormDTO;
import com.apihub.user.model.entity.User;
import com.apihub.user.model.entity.UserBalancePayment;
import com.apihub.user.model.vo.UserKeyPairVO;
import com.apihub.user.model.vo.UserLoginVO;
import com.apihub.user.model.vo.UserVO;
import com.apihub.user.service.UserService;
import com.apihub.user.utils.JwtTool;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.apihub.common.utils.RedisConstants.*;
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
@EnableConfigurationProperties(JwtProperties.class)
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private UserBalancePaymentServiceImpl userBalancePaymentService;
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
    public UserLoginVO login(LoginFormDTO loginFormDTO) {
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
            log.info("用户不存在或密码错误");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }

        if (user.getUserRole().equals(BAN_ROLE)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "被封禁");
        }

        // 5.生成TOKEN
        String token = jwtTool.createToken(user.getId(), jwtProperties.getTokenTTL());

        // 6.封装VO
        UserLoginVO userLoginVo;
        userLoginVo = BeanUtil.copyProperties(user, UserLoginVO.class);

        //在redis中，以accessKey为Key，存储sign
        String accessKey = user.getAccessKey();
        //秘钥加密
//        Digester md5 = new Digester(DigestAlgorithm.SHA256);
//        String sign = "hekmatyar" + "." + user.getSecretKey();
//        sign = md5.digestHex(sign);
//        user.setSecretKey(sign);

        //todo 把用户余额信息也存储到redis中
        //7.保存所有用户信息到 redis中
        Map<String, Object> userMap = BeanUtil.beanToMap(user, new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreNullValue(true)
                        .setFieldValueEditor((fieldName, fieldValue) -> fieldValue != null ? fieldValue.toString() : ""));

        // todo 这里应该是，发请求的时候
        String key = LOGIN_USER_KEY + user.getId();
        //以userId为key存一份
        stringRedisTemplate.opsForHash().putAll(key, userMap);
        // 7.4.设置token有效期
        stringRedisTemplate.expire(key, LOGIN_USER_TTL, TimeUnit.MINUTES);

        //以AK为key存一份(两个作用，存在的话就是刷新有效期，不存在的话就是补充)
        HashMap<String, Object> userAkInfo = new HashMap<>();
        userAkInfo.put("id",String.valueOf(user.getId()));
        userAkInfo.put("secretKey",user.getSecretKey());
        stringRedisTemplate.opsForHash().putAll(API_ACCESS_KEY + accessKey, userAkInfo);



        // 保存用户余额
        String blcKey = USER_BALANCE_KEY + user.getId();

        // 查询balance数据库(UserBalancePaymentServiceImpl有getBalance函数，但是是通过UserHolder获取用户id的)
        // 网关没有拦截，也就没有传递user-id，此时UserHolder里面就没有user-id
        QueryWrapper<UserBalancePayment> balanceQuery = new QueryWrapper<>();
        balanceQuery.eq("userId", user.getId());
        UserBalancePayment balanceInfo = userBalancePaymentService.getOne(balanceQuery);


        stringRedisTemplate.opsForValue().set(blcKey , String.valueOf(balanceInfo.getBalance()));


        //Todo 可将此时间设置长一些
        stringRedisTemplate.expire(API_ACCESS_KEY + accessKey,LOGIN_USER_TTL,TimeUnit.MINUTES);

        // 用户余额的TTL设置成和登录一样，因为用户仅在登陆期间才能看到余额
        stringRedisTemplate.expire(blcKey, LOGIN_USER_TTL, TimeUnit.MINUTES);


        // 返回token+user信息
        userLoginVo.setToken(token);

        return userLoginVo;
    }

    @Override
    public UserVO getLoginUser() {
        Long userId = UserHolder.getUser();
        if (userId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "未登录");
        }

        String tokenKey = LOGIN_USER_KEY + userId;

        //如果不想用redis,将下列代码改造即可
        //尝试使用redis查询用户信息，如果没有查询到，则通过MySQL查询到后，重新生成到redis中
        Map<Object, Object> userMap;
        try {
            userMap = stringRedisTemplate.opsForHash().entries(tokenKey);
        } catch (Exception e) {
            //throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "未登录");
            User user = this.getById(userId);
            if (user == null) {
                log.info("用户不存在");
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
            }
            if (user.getUserRole().equals(BAN_ROLE)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "被封禁");
            }
            UserVO userVo;
            userVo = BeanUtil.copyProperties(user, UserVO.class);

            //不想使用redis，把这个注释掉
            Map<String, Object> userVoMap = BeanUtil.beanToMap(userVo, new HashMap<>(),
                    CopyOptions.create()
                            .setIgnoreNullValue(true)
                            .setFieldValueEditor((fieldName, fieldValue) -> fieldValue != null ? fieldValue.toString() : ""));

            String key = LOGIN_USER_KEY + user.getId();
            stringRedisTemplate.opsForHash().putAll(key, userVoMap);
            // 设置token有效期
            stringRedisTemplate.expire(key, LOGIN_USER_TTL, TimeUnit.MINUTES);

            return userVo;
        }
        if (userMap.isEmpty()) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "用户数据为空");
        }

//

        if (userMap.get("userRole").equals(BAN_ROLE)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "被封禁");
        }
        //刷新redis存储时间
        stringRedisTemplate.expire(tokenKey, LOGIN_USER_TTL, TimeUnit.MINUTES);

        return BeanUtil.fillBeanWithMap(userMap, new UserVO(), false);
    }

    @Override
    public Boolean checkUserAK(String accessKey, String sign) {
        //获取用户，并检查是否封禁
        Long userId = UserHolder.getUser();
        if (userId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "未登录");
        }

        String tokenKey = LOGIN_USER_KEY + userId;

        Map<Object, Object> userMap;
        userMap = stringRedisTemplate.opsForHash().entries(tokenKey);
        if (userMap.isEmpty()) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "用户数据为空");
        }
        if (userMap.get("userRole").equals(BAN_ROLE)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "被封禁");
        }


        return Objects.equals(sign, stringRedisTemplate.opsForValue().get(API_ACCESS_KEY + accessKey));
    }

    @Override
    public UserKeyPairVO changeKeyPair(LoginFormDTO loginFormDTO) {
        if(loginFormDTO == null || StringUtils.isBlank(loginFormDTO.getUserPassword()) )
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数有误");
        }

        // 获取当前登录用户信息(拿到userAccount)
//        UserVO loginUser = getLoginUser();

//        String userAccount = loginUser.getUserAccount();
        String userAccount = "root";
        String userPassword = loginFormDTO.getUserPassword();
        // 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((MD5_SALT + userPassword).getBytes());

        // 生成一份新的ak/sk
        String accessKey = DigestUtil.md5Hex(MD5_SALT + userAccount + RandomUtil.randomNumbers(5));
        String secretKey = DigestUtil.md5Hex(MD5_SALT + userAccount + RandomUtil.randomNumbers(8));


        //  update user
        //  set accessKey = ?  and secretKey = ?
        //  where  userAccount = ?  and userPassword = ?
        UpdateWrapper<User> wrapper = new UpdateWrapper<>();
        wrapper.eq("userAccount", userAccount);
        wrapper.eq("userPassword", encryptPassword);
        wrapper.set("accessKey", accessKey);
        wrapper.set("secretKey",secretKey);

        boolean update = this.update(wrapper);
        if(!update)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }

        UserKeyPairVO keyPairVO = new UserKeyPairVO();
        keyPairVO.setSecreteKey(secretKey);
        keyPairVO.setAccessKey(accessKey);

        return keyPairVO;
    }
}
