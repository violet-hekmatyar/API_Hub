package com.apihub.pay.controller;


import com.apihub.common.common.BaseResponse;
import com.apihub.common.common.ErrorCode;
import com.apihub.common.common.ResultUtils;
import com.apihub.common.exception.BusinessException;
import com.apihub.pay.model.dto.ChargePayDTO;
import com.apihub.pay.model.dto.DeductPayDTO;
import com.apihub.pay.model.enums.PayType;
import com.apihub.pay.service.PayOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = "支付单接口")
@RestController
@Slf4j
@RequestMapping("/payOrder")
public class PayOrderController {

    @Resource
    private PayOrderService payOrderService;

    @ApiOperation("充值支付单")
    @PostMapping("/charge")
    public BaseResponse<Boolean> chargePayOrder(@RequestBody ChargePayDTO chargePayDTO) {
        if (chargePayDTO.getAmount() == null || chargePayDTO.getAmount() < 1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "充值数额不合法");
        }
        payOrderService.chargePayOrder(chargePayDTO);

        return ResultUtils.success(true);
    }

    @ApiOperation("生成支付单")
    @PostMapping("/deduct")
    public String deductPayOrder(@RequestBody DeductPayDTO deductPayDTO) {
        if (!PayType.BALANCE.equalsValue(deductPayDTO.getPayType())) {
            // 目前只支持余额支付
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "仅支持余额支付");
        }
//        return payOrderService.deductPayOrder(deductPayDTO);
        return null;
    }
}
