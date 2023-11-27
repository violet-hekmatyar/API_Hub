package com.apihub.voucher.controller;


import com.apihub.common.common.BaseResponse;
import com.apihub.voucher.service.VoucherOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Slf4j
@RequestMapping("/voucherPay")
public class VoucherPayController {

    @Resource
    private VoucherOrderService voucherOrderService;

    @PostMapping("/")
    public BaseResponse<Boolean> VoucherPay() {

        //return voucherSeckillService.seckillVoucher(voucherId);
        return null;
    }
}
