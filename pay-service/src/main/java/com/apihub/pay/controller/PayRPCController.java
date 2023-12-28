package com.apihub.pay.controller;

import com.apihub.common.common.BaseResponse;
import com.apihub.common.common.ErrorCode;
import com.apihub.common.common.ResultUtils;
import com.apihub.common.exception.BusinessException;
import com.apihub.pay.model.dto.APIDeduct;
import com.apihub.pay.model.dto.ChargePayDTO;
import com.apihub.pay.service.PayOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

//此接口仅系统内部使用，不可让用户自己调用
@Api(tags = "支付单rpc接口")
@RestController
@Slf4j
@RequestMapping("/payRPC")
public class PayRPCController {
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

    @ApiOperation("api调用扣减余额")
    @PostMapping("/deduct/balance")
    public BaseResponse<Boolean> APIDeductByBalance(@RequestBody APIDeduct APIDeduct, HttpServletRequest request) {
        payOrderService.apiDeductByBalance(APIDeduct, request);
        return ResultUtils.success(true);
    }

    //不支持手动创建支付单
}
