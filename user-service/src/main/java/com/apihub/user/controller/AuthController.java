package com.apihub.user.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import com.apihub.common.common.BaseResponse;
import com.apihub.common.common.ErrorCode;
import com.apihub.common.common.ResultUtils;
import com.apihub.common.exception.BusinessException;
import com.apihub.common.utils.UserHolder;
import com.apihub.user.model.dto.aouth.BindGiteeRequest;
import com.apihub.user.model.dto.aouth.GiteeTokenRequest;
import com.apihub.user.model.dto.aouth.GiteeUserInfoResponse;
import com.apihub.user.model.entity.User;
import com.apihub.user.model.vo.UserLoginVO;
import com.apihub.user.model.vo.UserVO;
import com.apihub.user.service.UserService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xkcoding.justauth.AuthRequestFactory;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.enums.AuthResponseStatus;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.utils.AuthStateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.apihub.common.utils.RedisConstants.LOGIN_USER_KEY;

@RestController
@Slf4j
@RequestMapping("/user/oauth")
public class AuthController {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private UserService userService;

    @Autowired
    private AuthRequestFactory factory;

    @GetMapping
    public List<String> list() {
        return factory.oauthList();
    }

    @Value("${justauth.type.GITEE.client-id}")
    private String clientId;

    @Value("${justauth.type.GITEE.client-secret}")
    private String clientSecret;

    //用户通过gitee登录
    @GetMapping("/login/gitee")
    public void giteeLogin(HttpServletResponse response) throws IOException {
        AuthRequest authRequest = factory.get("gitee");
        response.sendRedirect(authRequest.authorize(AuthStateUtils.createState()));
    }

    //gitee回调后，返回token和用户信息，并执行登录操作
    @GetMapping("/gitee/callback")
    public BaseResponse<UserLoginVO> login(AuthCallback callback) {
        AuthRequest authRequest = factory.get("gitee");
        AuthResponse response = authRequest.login(callback);
        if (response.getCode() != AuthResponseStatus.SUCCESS.getCode()) {
            log.error(response.getCode() + ":" + response.getMsg());
            throw new BusinessException(response.getCode(), response.getMsg());
        }
        return ResultUtils.success(userService.giteeLoginCallback(response));
    }

    //已登录用户绑定gitee
    //通过输入gitee邮箱和密码
    //将token和refreshToken返回
    @GetMapping("/bind/gitee")
    public BaseResponse<UserVO> bindGitee(@RequestBody BindGiteeRequest bindGiteeRequest) {

        Long userId = UserHolder.getUser();
        if (userId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        if (bindGiteeRequest == null || bindGiteeRequest.getEmail() == null || bindGiteeRequest.getPassword() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        GiteeTokenRequest giteeTokenRequest = new GiteeTokenRequest();
        giteeTokenRequest.setClient_id(clientId);
        giteeTokenRequest.setClient_secret(clientSecret);
        giteeTokenRequest.setUsername(bindGiteeRequest.getEmail());
        giteeTokenRequest.setPassword(bindGiteeRequest.getPassword());
        giteeTokenRequest.setScope("user_info");
        giteeTokenRequest.setGrant_type("password");

        Gson gson = new Gson();
        String giteeTokenRequestJson = gson.toJson(giteeTokenRequest);

        String giteeTokenResponse = HttpRequest.post("https://gitee.com/oauth/token")
                .body(giteeTokenRequestJson)
                .execute().body();

        Map<String, String>
                giteeTokenResponseMap = gson.fromJson(giteeTokenResponse, new TypeToken<Map<String, String>>() {
        }.getType());
        String access_token = giteeTokenResponseMap.get("access_token");
        if (access_token == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱或密码错误，或服务器错误");
        }

        String userInfoResponse = HttpRequest.get("https://gitee.com/api/v5/user?access_token=" + access_token)
                .timeout(10000)
                .execute().body();
        log.info(userInfoResponse);

        GiteeUserInfoResponse giteeUserInfo = gson.fromJson(userInfoResponse, GiteeUserInfoResponse.class);

        String tokenKey = LOGIN_USER_KEY + userId;
        Map<Object, Object> userMap;
        User user;
        try {
            userMap = stringRedisTemplate.opsForHash().entries(tokenKey);
            user = BeanUtil.fillBeanWithMap(userMap, new User(), false);
        } catch (Exception e) {
            //throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "未登录");
            user = userService.getById(userId);
        }

        user.setUnionId(String.valueOf(giteeUserInfo.getId()));

        //将gitee的个人信息迁移到apihub中
        if (bindGiteeRequest.getMode() == 1) {
            if (StrUtil.isBlank(user.getUserAvatar())) user.setUserAvatar(giteeUserInfo.getAvatar_url());
            if (StrUtil.isBlank(user.getUserName())) user.setUserName(giteeUserInfo.getName());
        }

        boolean b = userService.updateById(user);
        if (!b) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "MySQL绑定Gitee失败");
        }


        //redis中的用户信息更新
        Map<String, Object> newUserMap = BeanUtil.beanToMap(user, new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreNullValue(true)
                        .setFieldValueEditor((fieldName, fieldValue) -> fieldValue != null ? fieldValue.toString() : ""));

        String key = LOGIN_USER_KEY + user.getId();
        stringRedisTemplate.opsForHash().putAll(key, newUserMap);

        //封装VO
        UserVO userVo;
        userVo = BeanUtil.copyProperties(user, UserVO.class);

        return ResultUtils.success(userVo);
    }

    //通过refreshToken获得用户信息，并更新到用户信息中

//    【response】= {"code":2000,"data":{"uuid":"11781876","username":"violet_hekmatyar","nickname":"hekmatyar_violet","avatar":"https://foruda.gitee.com/avatar/1690893589879244288/11781876_violet_hekmatyar_1690893589.png","blog":"http://blog.hekmatyar.cn","remark":"","gender":"UNKNOWN","source":"GITEE","token":{"accessToken":"3514e1eaed73eb9956d2339474079a41","expireIn":86400,"refreshToken":"73b9ae65a2904c5cdbab3e55b48cc9ae0ecb5f1a2a635daf053b61e2a0334c05","refreshTokenExpireIn":0,"scope":"user_info","tokenType":"bearer"},"rawUserInfo":{"gists_url":"https://gitee.com/api/v5/users/violet_hekmatyar/gists{/gist_id}","repos_url":"https://gitee.com/api/v5/users/violet_hekmatyar/repos","following_url":"https://gitee.com/api/v5/users/violet_hekmatyar/following_url{/other_user}","bio":"","created_at":"2022-10-17T11:56:54+08:00","remark":"","login":"violet_hekmatyar","type":"User","blog":"http://blog.hekmatyar.cn","subscriptions_url":"https://gitee.com/api/v5/users/violet_hekmatyar/subscriptions","updated_at":"2024-04-03T09:42:41+08:00","id":11781876,"public_repos":23,"organizations_url":"https://gitee.com/api/v5/users/violet_hekmatyar/orgs","starred_url":"https://gitee.com/api/v5/users/violet_hekmatyar/starred{/owner}{/repo}","followers_url":"https://gitee.com/api/v5/users/violet_hekmatyar/followers","public_gists":0,"url":"https://gitee.com/api/v5/users/violet_hekmatyar","received_events_url":"https://gitee.com/api/v5/users/violet_hekmatyar/received_events","watched":31,"followers":3,"avatar_url":"https://foruda.gitee.com/avatar/1690893589879244288/11781876_violet_hekmatyar_1690893589.png","events_url":"https://gitee.com/api/v5/users/violet_hekmatyar/events{/privacy}","html_url":"https://gitee.com/violet_hekmatyar","following":3,"name":"hekmatyar_violet","stared":13}}}

    /*@GetMapping("/login/{type}")
    public void login(@PathVariable String type, HttpServletResponse response) throws IOException {
        AuthRequest authRequest = factory.get(type);
        response.sendRedirect(authRequest.authorize(AuthStateUtils.createState()));
    }*/

}
