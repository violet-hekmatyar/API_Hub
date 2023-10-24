package com.apihub.interfaceInfo.client;

import com.apihub.interfaceInfo.model.vo.UserVO;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("user-service")
public interface InterfaceInfoClient {

    @PostMapping("/api/userRPC/get/login")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    UserVO getCurrentUser(@RequestParam("token") String token);
}
