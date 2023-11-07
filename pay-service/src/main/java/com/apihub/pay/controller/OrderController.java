package com.apihub.pay.controller;


import com.apihub.pay.model.dto.DeductOrderDTO;
import com.apihub.pay.service.ApiOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = "订单接口")
@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController {

    @Resource
    private ApiOrderService apiOrderService;

    @ApiOperation("支付订单")
    @PostMapping("/deduct")
    public Boolean deductOrder(@RequestBody DeductOrderDTO deductOrderDTO){
        return apiOrderService.deductOrder(deductOrderDTO);
    }
}
