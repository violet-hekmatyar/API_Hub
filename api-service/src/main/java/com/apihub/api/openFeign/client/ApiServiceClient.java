package com.apihub.api.openFeign.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


//Todo 扣减用户使用次数
@FeignClient("user-service")
public interface ApiServiceClient {
    //获取用户apiKey
    @PostMapping("/api/userRPC/get/apikey")
    Boolean getApiKey(@RequestParam("ak") String ak,@RequestParam("sign") String sign);
}
