package com.apihub.gateway.filter;

import cn.hutool.core.util.StrUtil;
import com.apihub.common.utils.CollUtils;
import com.apihub.gateway.config.AuthProperties;
import com.apihub.gateway.utils.ApiSignUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.apihub.common.utils.RedisConstants.API_ACCESS_KEY;


@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(AuthProperties.class)
public class AKGlobalFilter implements GlobalFilter, Ordered {

    private final AuthProperties authProperties;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1.获取Request
        ServerHttpRequest request = exchange.getRequest();
        // 2.判断是否不需要拦截
        if (!isInclude(request.getPath().toString())) {
            // 无需拦截，直接放行
            return chain.filter(exchange);
        }
        List<String> headers;
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
        if (!StrUtil.isAllNotEmpty(accessKey, secretKey, sign)){
            // 如果无效，拦截
            ServerHttpResponse response = exchange.getResponse();
            response.setRawStatusCode(403);
            return response.setComplete();
        }
        String userId = null;
        try{
            Map<Object, Object> userMap = stringRedisTemplate.opsForHash().entries(API_ACCESS_KEY + accessKey);
            String userSign = (String) userMap.get("secretKey");
            if (!Objects.equals(userSign, sign))throw new RuntimeException();

            //传递userId
            userId = (String) userMap.get("id");
            String finalUserId = userId;
            ServerWebExchange ex = exchange.mutate()
                    .request(b -> b.header("userId-info", String.valueOf(finalUserId)))
                    .build();
        }catch (Exception e){
            ServerHttpResponse response = exchange.getResponse();
            response.setRawStatusCode(403);
            return response.setComplete();
        }


        /*ServerWebExchange ex = exchange.mutate()
                .request(b -> b.header("accessKey", accessKey))
                .request(b -> b.header("sign", sign))
                .build();*/

        // 放行
        return chain.filter(exchange);
    }

    private boolean isInclude(String antPath) {
        for (String pathPattern : authProperties.getIncludePaths()) {
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
