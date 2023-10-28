package com.apihub.api.controller;


import com.apihub.api.model.domain.InterfaceInfo;
import com.apihub.api.openFeign.client.InterfaceInfoServiceClient;
import com.apihub.common.common.BaseResponse;
import com.apihub.common.common.ErrorCode;
import com.apihub.common.common.ResultUtils;
import com.apihub.common.exception.BusinessException;
import com.apihub.common.utils.UserHolder;
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
    private final InterfaceInfoServiceClient interfaceInfoServiceClient;

    @ApiOperation("获取接口信息+校验")
    @GetMapping("/get")
    public BaseResponse<Object> getInterfaceInfoById(HttpServletRequest request) {
        Long userId = UserHolder.getUser();
        String method = request.getHeader("method");
        String interfaceId = request.getHeader("interfaceId");
        System.out.println("------------" + userId +"-----------"+ method);
        InterfaceInfo interfaceInfo =  interfaceInfoServiceClient.queryItemById(Long.parseLong(interfaceId));
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未查询到有效接口");
        }
        return ResultUtils.success(interfaceInfo);
    }
}
