package com.apihub.user.controller;

import com.apihub.user.service.UserBalancePaymentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = "用户余额RPC接口")
@RestController
@Slf4j
@RequestMapping("/user/balance")
public class userBalanceRPCController {

    @Resource
    private UserBalancePaymentService userBalancePaymentService;

    @ApiOperation("余额扣减")
    @PutMapping("/deduct")
    public Boolean deductBalance(@RequestParam("amount") Integer amount){
        return userBalancePaymentService.deductBalance(amount);
    }

    @ApiOperation("余额充值")
    @PutMapping("/charge")
    public Boolean chargeBalance(@RequestParam("amount") Integer amount){
        return userBalancePaymentService.chargeBalance(amount);
    }

}
