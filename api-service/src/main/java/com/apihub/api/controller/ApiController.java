package com.apihub.api.controller;


import com.apihub.api.openFeign.client.ApiServiceClient;
import com.apihub.common.common.BaseResponse;
import com.apihub.common.common.ResultUtils;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/apiService")
@Slf4j
@RequiredArgsConstructor
public class ApiController {
    private final ApiServiceClient apiServiceClient;

    @ApiOperation("get第三方接口调用")
    @GetMapping("/get")
    public BaseResponse<Object> getInterfaceInfoById(HttpServletRequest request) {
        String accessKey = request.getHeader("accessKey");
        String sign = request.getHeader("sign");
        System.out.println("--------------------------------------");
        System.out.println(accessKey);
        System.out.println(sign);
        return ResultUtils.success(apiServiceClient.getApiKey(accessKey,sign));
    }
}
