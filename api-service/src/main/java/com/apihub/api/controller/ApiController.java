package com.apihub.api.controller;


import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.apihub.api.model.domain.InterfaceInfo;
import com.apihub.api.model.dto.DeductOrderMqDTO;
import com.apihub.api.openFeign.client.InterfaceInfoServiceClient;
import com.apihub.api.openFeign.client.UserServiceClient;
import com.apihub.common.common.BaseResponse;
import com.apihub.common.common.ErrorCode;
import com.apihub.common.common.ResultUtils;
import com.apihub.common.exception.BusinessException;
import com.apihub.common.utils.UserHolder;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/apiService")
@Slf4j
@RequiredArgsConstructor
public class ApiController {
    private final InterfaceInfoServiceClient interfaceInfoServiceClient;
    private final UserServiceClient userServiceClient;

    private final RabbitTemplate rabbitTemplate;

    //todo 需要开启全局事务
    @ApiOperation("get请求接口")
    @GetMapping("/get")
    public BaseResponse<Object> getInterfaceInfoById(@RequestParam("InterfaceId") long interfaceId,
                                                     @RequestParam("body") String body,
                                                     HttpServletRequest request) {
        //查询接口url及其他信息
        InterfaceInfo interfaceInfo = interfaceInfoServiceClient.queryItemById(interfaceId);

        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未查询到有效接口");
        }
        if (interfaceInfo.getId() == 0) {
            //System.out.println("id===0---------------------------------------------");
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "服务器繁忙，请稍后重试");
        }
        if (!interfaceInfo.getMethod().contains("get")) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求方法错误");
        }
        Integer interfaceInfoPrice = interfaceInfo.getPrice();
        //对于第三方接口，不检查是否符合请求规范

        //检查用户余额，并进行扣款
        if (!userServiceClient.deductBalance(interfaceInfoPrice)) {
            //如果不够次数，直接返回结果
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "用户余额不足");
        }

        //向url发请求
        String requestBody = URLUtil.decode(body, CharsetUtil.CHARSET_UTF_8);
        HttpResponse httpResponse = HttpRequest.get(interfaceInfo.getUrl() + "?" + requestBody)
                .execute();

        //请求成功，使用MQ队列
        try {
            DeductOrderMqDTO deductOrderMqDTO = new DeductOrderMqDTO();
            deductOrderMqDTO.setInterfaceId(interfaceInfo.getId());
            deductOrderMqDTO.setNum(1L);
            deductOrderMqDTO.setPaymentType(3);
            deductOrderMqDTO.setTotalFee(interfaceInfoPrice);
            //返回发送请求的客户端或最后一个代理的Internet协议（IP）地址
            deductOrderMqDTO.setUserAddress(request.getRemoteAddr());
            deductOrderMqDTO.setUserId(UserHolder.getUser());
            log.info("发送MQ队列请求");
            rabbitTemplate.convertAndSend("payService.topic", "order.success", deductOrderMqDTO);
        }catch (Exception e){
            log.error("MQ队列出错，订单发送失败：",e);
        }

        return ResultUtils.success(httpResponse.body());
    }

    @ApiOperation("post请求接口")
    @PostMapping("/post")
    public BaseResponse<Object> postInterfaceInfoById(@RequestParam("InterfaceId") long interfaceId,
                                                     @RequestParam("body") String body,
                                                     HttpServletRequest request) {
        Long userId = UserHolder.getUser();
        //查询接口url及其他信息
        InterfaceInfo interfaceInfo = interfaceInfoServiceClient.queryItemById(interfaceId);

        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未查询到有效接口");
        }
        if (interfaceInfo.getId() == 0) {
            //System.out.println("id===0---------------------------------------------");
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "服务器繁忙，请稍后重试");
        }
        if (!interfaceInfo.getMethod().contains("post")) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求方法错误");
        }
        Integer interfaceInfoPrice = interfaceInfo.getPrice();
        //检查用户余额，并进行扣款
        if (!userServiceClient.deductBalance(interfaceInfoPrice)) {
            //如果不够次数，直接返回结果
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "用户余额不足");
        }

        //向url发请求
        String requestBody = URLUtil.decode(body, CharsetUtil.CHARSET_UTF_8);
        HttpResponse httpResponse = HttpRequest.post(interfaceInfo.getUrl() + "?" + requestBody)
                .execute();

        //请求成功，使用MQ队列
        //仅记录订单
        //使用try catch，因为错误记录可以人工更改，但是钱必须先扣
        try {
            DeductOrderMqDTO deductOrderMqDTO = new DeductOrderMqDTO();
            deductOrderMqDTO.setInterfaceId(interfaceInfo.getId());
            deductOrderMqDTO.setNum(1L);
            deductOrderMqDTO.setPaymentType(3);
            deductOrderMqDTO.setTotalFee(interfaceInfoPrice);
            //返回发送请求的客户端或最后一个代理的Internet协议（IP）地址
            deductOrderMqDTO.setUserAddress(request.getRemoteAddr());
            deductOrderMqDTO.setUserId(UserHolder.getUser());
            log.info("发送MQ队列请求");
            rabbitTemplate.convertAndSend("payService.topic", "order.success", deductOrderMqDTO);
        } catch (Exception e) {
            log.error("MQ队列出错，订单发送失败：", e);
        }

        return ResultUtils.success(httpResponse.body());
    }
}
