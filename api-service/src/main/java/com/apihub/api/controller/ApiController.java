package com.apihub.api.controller;


import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@RestController
@RequestMapping("/apiService")
@Slf4j
@RequiredArgsConstructor
public class ApiController {
    private final InterfaceInfoServiceClient interfaceInfoServiceClient;

    @ApiOperation("获取接口信息+校验")
    @GetMapping("/get")
    public BaseResponse<Object> getInterfaceInfoById(@RequestParam("InterfaceId") long interfaceId,
                                                     @RequestParam("body") String body,
                                                     HttpServletRequest request) {
        Long userId = UserHolder.getUser();
        //查询接口url及其他信息
        InterfaceInfo interfaceInfo =  interfaceInfoServiceClient.queryItemById(interfaceId);

        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未查询到有效接口");
        }
        if (interfaceInfo.getId()==0){
            System.out.println("id===0---------------------------------------------");
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "服务器繁忙，请稍后重试");
        }
        if(!Objects.equals(interfaceInfo.getMethod(), "get")){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求方法错误");
        }
        //todo 查询是否符合请求规范
        //是否包含必须的数据，如果没有，返回错误

        //todo 查询用户可用次数---带价格
        //如果不够次数，直接返回结果

        //todo 根据interface的类别进行分别操作


        //向url发请求
        String requestBody = URLUtil.decode(body, CharsetUtil.CHARSET_UTF_8);
        HttpResponse httpResponse = HttpRequest.get(interfaceInfo.getUrl() + "?" + requestBody)
                .execute();

        //todo 扣减用户额度
        //请求成功，扣钱，使用MQ队列

        return ResultUtils.success(httpResponse.body());
    }
}
