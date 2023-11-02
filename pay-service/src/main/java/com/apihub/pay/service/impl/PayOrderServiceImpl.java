package com.apihub.pay.service.impl;

import com.apihub.common.common.ErrorCode;
import com.apihub.common.exception.BusinessException;
import com.apihub.common.utils.BeanUtils;
import com.apihub.common.utils.UserHolder;
import com.apihub.pay.mapper.PayOrderMapper;
import com.apihub.pay.model.dto.ChargePayDTO;
import com.apihub.pay.model.entity.PayOrder;
import com.apihub.pay.model.enums.PayStatus;
import com.apihub.pay.openFeign.client.PayServiceClient;
import com.apihub.pay.service.PayOrderService;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Calendar;

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

        //直接支付成功
        calendar = Calendar.getInstance();
        initPayOrder.setPaySuccessTime(calendar.getTime());
        initPayOrder.setStatus(PayStatus.TRADE_SUCCESS.getValue());
        initPayOrder.setBizUserId(UserHolder.getUser());

        this.save(initPayOrder);

        payServiceClient.chargeBalance(initPayOrder.getAmount());
    }

    private PayOrder checkIdempotent(PayOrder newPayOrder) {
        // 1.首先查询支付单
        PayOrder oldOrder = queryByBizOrderNo(newPayOrder.getBizOrderNo());
        // 2.判断是否存在
        if (oldOrder == null) {
            // 不存在支付单，说明是第一次，写入新的支付单并返回
            PayOrder payOrder = buildPayOrder(newPayOrder);
            payOrder.setPayOrderNo(IdWorker.getId());
            save(payOrder);
            return payOrder;
        }
        // 3.旧单已经存在，判断是否支付成功
        if (PayStatus.TRADE_SUCCESS.equalsValue(oldOrder.getStatus())) {
            // 已经支付成功，抛出异常
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"订单已经支付");
        }
        // 4.旧单已经存在，判断是否已经关闭
        if (PayStatus.TRADE_CLOSED.equalsValue(oldOrder.getStatus())) {
            // 已经关闭，抛出异常
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"订单已经关闭");
        }
        /*// 5.旧单已经存在，判断支付渠道是否一致
        if (!StringUtils.equals(oldOrder.getPayChannelCode(), newPayOrder.getPayChannelCode())) {
            // 支付渠道不一致，需要重置数据，然后重新申请支付单
            PayOrder payOrder = buildPayOrder(newPayOrder);
            payOrder.setId(oldOrder.getId());
            payOrder.setQrCodeUrl("");
            updateById(payOrder);
            payOrder.setPayOrderNo(oldOrder.getPayOrderNo());
            return payOrder;
        }*/
        // 6.旧单已经存在，且可能是未支付或未提交，且支付渠道一致，直接返回旧数据
        return oldOrder;
    }

    private PayOrder buildPayOrder(PayOrder payOrder) {
        // 1.数据转换
        PayOrder initPayOrder = BeanUtils.toBean(payOrder, PayOrder.class);
        // 2.初始化数据
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 120);
        initPayOrder.setPayOverTime(calendar.getTime());

        initPayOrder.setStatus(PayStatus.WAIT_BUYER_PAY.getValue());
        initPayOrder.setBizUserId(UserHolder.getUser());
        return initPayOrder;
    }
    public PayOrder queryByBizOrderNo(Long bizOrderNo) {
        return lambdaQuery()
                .eq(PayOrder::getBizOrderNo, bizOrderNo)
                .one();
    }
}




