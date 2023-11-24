package com.apihub.api.openFeign.client;

import com.apihub.api.openFeign.config.DefaultFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(value = "user-service",
        configuration = DefaultFeignConfig.class)
public interface UserServiceClient {

    //获取接口信息
    @GetMapping("/api/user/balance/deduct")
    Boolean deductBalance(@RequestParam("amount") Integer amount);
}
