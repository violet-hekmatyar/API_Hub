package com.apihub.voucher.controller;


import com.apihub.common.common.BaseResponse;
import com.apihub.common.common.ResultUtils;
import com.apihub.voucher.model.dto.SeckillVoucherInfoAddRequest;
import com.apihub.voucher.service.VoucherInfoService;
import com.apihub.voucher.service.VoucherSeckillService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

//秒杀优惠券信息CRUD
@RestController
@Slf4j
@RequestMapping("/voucherInfo/seckill")
public class SeckillVoucherInfoController {

    @Resource
    private VoucherInfoService voucherInfoService;

    @Resource
    private VoucherSeckillService voucherSeckillService;

    //限量优惠券即秒杀优惠券
    @ApiOperation("添加秒杀优惠券信息")
    @PostMapping("/add/seckill")
    public BaseResponse<Boolean> addSeckillVoucherInfo(@RequestBody SeckillVoucherInfoAddRequest seckillVoucherInfoAddRequest,
                                                       HttpServletRequest request) {

        voucherInfoService.saveSeckillVoucherInfo(seckillVoucherInfoAddRequest, request);

        return ResultUtils.success(true);
    }
}
