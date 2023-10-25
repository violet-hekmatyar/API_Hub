package com.apihub.interfaceInfo.openFeign.client;

import com.apihub.interfaceInfo.model.vo.UserVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient("user-service")
public interface InterfaceInfoClient {

    @GetMapping("/api/userRPC/get/login")
    UserVO getCurrentUser();

    @GetMapping("/api/userRPC/checkAdmin")
    Boolean checkAdmin();
}
