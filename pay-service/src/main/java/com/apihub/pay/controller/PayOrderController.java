package com.apihub.pay.controller;


import com.apihub.common.common.BaseResponse;
import com.apihub.common.common.ErrorCode;
import com.apihub.common.common.ResultUtils;
import com.apihub.common.exception.BusinessException;
import com.apihub.common.utils.ThrowUtils;
import com.apihub.pay.model.dto.ChargePayDTO;
import com.apihub.pay.model.dto.DeductPayDTO;
import com.apihub.pay.model.dto.PayOrderQueryRequest;
import com.apihub.pay.model.entity.PayOrder;
import com.apihub.pay.model.enums.PayType;
import com.apihub.pay.model.vo.PayOrderVO;
import com.apihub.pay.service.PayOrderService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

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
    public Boolean deductPayOrder(@RequestBody DeductPayDTO deductPayDTO) {
        if (!PayType.BALANCE.equalsValue(deductPayDTO.getPayType())) {
            // 目前只支持余额支付
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "仅支持余额支付");
        }
//        return payOrderService.deductPayOrder(deductPayDTO);
        return null;
    }

    @ApiOperation("分页搜索支付单")
    @GetMapping("/list/page")
    public BaseResponse<Page<PayOrderVO>> listPayOrderByPage(@RequestBody PayOrderQueryRequest payOrderQueryRequest) {


        return null;
    }

    @ApiOperation("分页搜索支付单")
    @GetMapping("/admin/list/page")
    public BaseResponse<Page<PayOrderVO>> adminListPayOrderByPage(@RequestBody PayOrderQueryRequest payOrderQueryRequest) {
        long current = payOrderQueryRequest.getCurrent();
        long size = payOrderQueryRequest.getPageSize();
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);

        PayOrder payOrderQuery = new PayOrder();
        BeanUtils.copyProperties(payOrderQueryRequest, payOrderQuery);
        QueryWrapper<PayOrder> queryWrapper = new QueryWrapper<>(payOrderQuery);

        Page<PayOrder> payOrders = payOrderService.page(new Page<>(current, size), queryWrapper);
        Page<PayOrderVO> payOrderVOPage = new PageDto<>(payOrders.getCurrent(), payOrders.getSize(), payOrders.getTotal());
        List<PayOrderVO> payOrderVOList = payOrders.getRecords().stream().map(payOrder -> {
            PayOrderVO payOrderVO = new PayOrderVO();
            BeanUtils.copyProperties(payOrder, payOrderVO);
            return payOrderVO;
        }).collect(Collectors.toList());
        payOrderVOPage.setRecords(payOrderVOList);
        return ResultUtils.success(payOrderVOPage);
    }
}
