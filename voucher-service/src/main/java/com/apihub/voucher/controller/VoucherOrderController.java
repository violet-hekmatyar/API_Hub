package com.apihub.voucher.controller;


import com.apihub.common.common.BaseResponse;
import com.apihub.voucher.model.vo.VoucherSeckillVO;
import com.apihub.voucher.service.VoucherSeckillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Slf4j
@RequestMapping("/voucherSeckill")
public class VoucherOrderController {

    @Resource
    private VoucherSeckillService voucherSeckillService;

    @PostMapping("/{id}")
    public BaseResponse<VoucherSeckillVO> seckillVoucher(@PathVariable("id") Long voucherId) {

        //return voucherSeckillService.seckillVoucher(voucherId);
        return null;
    }
}
