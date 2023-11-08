package com.apihub.pay.service.impl;

import com.apihub.common.common.ErrorCode;
import com.apihub.common.exception.BusinessException;
import com.apihub.common.utils.ThrowUtils;
import com.apihub.common.utils.UserHolder;
import com.apihub.pay.mapper.PayOrderMapper;
import com.apihub.pay.model.dto.ChargePayDTO;
import com.apihub.pay.model.dto.PayOrderQueryRequest;
import com.apihub.pay.model.entity.PayOrder;
import com.apihub.pay.model.enums.PayStatus;
import com.apihub.pay.model.vo.PayOrderVO;
import com.apihub.pay.openFeign.client.PayServiceClient;
import com.apihub.pay.service.PayOrderService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDto;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author IKUN
* @description 针对表【pay_order(支付订单)】的数据库操作Service实现
* @createDate 2023-11-02 11:58:00
*/
@Service
@Slf4j
@RequiredArgsConstructor
public class PayOrderServiceImpl extends ServiceImpl<PayOrderMapper, PayOrder>
    implements PayOrderService{

    private final PayServiceClient payServiceClient;

    //todo 完善充值等待
    //目前是直接充值，等有第三方支付后再完善
    @Override
    public void chargePayOrder(ChargePayDTO chargePayDTO) {
        // 1.数据转换
        PayOrder initPayOrder = new PayOrder();
        // 2.初始化数据
        initPayOrder.setPayType(6);
        initPayOrder.setAmount(chargePayDTO.getAmount());

        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.MINUTE, 15);
        initPayOrder.setPayOverTime(calendar.getTime());

        //todo 需要接入第三方支付
        //这里就直接支付成功
        calendar = Calendar.getInstance();
        initPayOrder.setPaySuccessTime(calendar.getTime());
        initPayOrder.setStatus(PayStatus.TRADE_SUCCESS.getValue());
        initPayOrder.setBizUserId(UserHolder.getUser());

        this.save(initPayOrder);

        payServiceClient.chargeBalance(initPayOrder.getAmount());
    }

    @Override
    public Page<PayOrderVO> listPayOrderByPage(PayOrderQueryRequest payOrderQueryRequest) {
        long current = payOrderQueryRequest.getCurrent();
        long size = payOrderQueryRequest.getPageSize();
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);

        Integer maxAmount = payOrderQueryRequest.getMaxAmount();
        Integer minAmount = payOrderQueryRequest.getMinAmount();

        PayOrder payOrderQuery = new PayOrder();
        BeanUtils.copyProperties(payOrderQueryRequest, payOrderQuery);
        QueryWrapper<PayOrder> queryWrapper = new QueryWrapper<>(payOrderQuery);

        //金额查询
        if (maxAmount != null) {
            queryWrapper.le("amount",maxAmount);
        }
        if (minAmount!=null){
            if (maxAmount!=null && maxAmount<minAmount) throw new BusinessException(ErrorCode.PARAMS_ERROR);
            queryWrapper.ge("amount",minAmount);
        }

        Page<PayOrder> payOrders = this.page(new Page<>(current, size), queryWrapper);
        Page<PayOrderVO> payOrderVOPage = new PageDto<>(payOrders.getCurrent(), payOrders.getSize(), payOrders.getTotal());
        List<PayOrderVO> payOrderVOList = payOrders.getRecords().stream().map(payOrder -> {
            PayOrderVO payOrderVO = new PayOrderVO();
            BeanUtils.copyProperties(payOrder, payOrderVO);
            return payOrderVO;
        }).collect(Collectors.toList());
        payOrderVOPage.setRecords(payOrderVOList);
        return payOrderVOPage;
    }

}




