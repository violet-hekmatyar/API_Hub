package com.apihub.gateway.filter;

import cn.hutool.core.util.StrUtil;
import com.apihub.common.utils.CollUtils;
import com.apihub.gateway.config.AuthProperties;
import com.apihub.gateway.utils.ApiSignUtils;
import com.apihub.gateway.utils.JwtTool;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;


@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(AuthProperties.class)
public class LoginGlobalFilter implements GlobalFilter, Ordered {

    private final JwtTool jwtTool;

    private final AuthProperties authProperties;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1.获取Request
        ServerHttpRequest request = exchange.getRequest();
        // 2.判断是否不需要拦截
        if (isExclude(request.getPath().toString())) {
            // 无需拦截，直接放行
            return chain.filter(exchange);
        }
        // 3.获取请求头中的token
        String token;
        List<String> headers = request.getHeaders().get("authorization");
        if (!CollUtils.isEmpty(headers)) {
            token = headers.get(0);
        } else {
            token = null;
        }
        // 4.校验并解析token
        Long userId;
        try {
            userId = jwtTool.parseToken(token);
        } catch (Exception e) {
            // 如果无效，拦截
            ServerHttpResponse response = exchange.getResponse();
            response.setRawStatusCode(401);
            return response.setComplete();
        }

        // token有效，传递用户信息
        String userIdInfo = userId.toString();

        //Ak和Sk的检查与传递
        //Todo 不登录的情况下，使用ak和sk也可以请求----现在需要登录且需要ak和sk
        //获取Ak和Sk
        String accessKey;
        String secretKey;
        headers = request.getHeaders().get("accessKey");
        if (!CollUtils.isEmpty(headers)) {
            accessKey = headers.get(0);
        } else {
            accessKey = null;
        }
        String sign;
        headers = request.getHeaders().get("secretKey");
        if (!CollUtils.isEmpty(headers)) {
            secretKey = headers.get(0);
            //签名方式
            sign = ApiSignUtils.genSign("hekmatyar", secretKey);
        } else {
            secretKey = null;
            sign = null;
        }


        if (StrUtil.isAllNotEmpty(accessKey, secretKey, sign)) {
            ServerWebExchange ex = exchange.mutate()
                    .request(b -> b.header("userId-info", userIdInfo))
                    .request(b -> b.header("accessKey", accessKey))
                    .request(b -> b.header("sign", sign))
                    .build();
        } else {
            ServerWebExchange ex = exchange.mutate()
                    .request(b -> b.header("userId-info", userIdInfo))
                    .build();
        }


        // 放行
        return chain.filter(exchange);
    }

    private boolean isExclude(String antPath) {
        for (String pathPattern : authProperties.getExcludePaths()) {
            if (antPathMatcher.match(pathPattern, antPath)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
