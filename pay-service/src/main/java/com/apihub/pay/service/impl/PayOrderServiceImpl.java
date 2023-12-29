package com.apihub.pay.service.impl;

import com.apihub.common.common.ErrorCode;
import com.apihub.common.exception.BusinessException;
import com.apihub.common.utils.ThrowUtils;
import com.apihub.common.utils.UserHolder;
import com.apihub.pay.mapper.PayOrderMapper;
import com.apihub.pay.model.dto.APIDeduct;
import com.apihub.pay.model.dto.ChargePayDTO;
import com.apihub.pay.model.dto.PayOrderQueryRequest;
import com.apihub.pay.model.dto.payvoucherorder.VoucherBalancePayDTO;
import com.apihub.pay.model.dto.payvoucherorder.VoucherOrderVO;
import com.apihub.pay.model.entity.PayOrder;
import com.apihub.pay.model.enums.PayStatus;
import com.apihub.pay.model.vo.PayOrderVO;
import com.apihub.pay.openFeign.client.UserServiceClient;
import com.apihub.pay.service.PayOrderService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDto;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.apihub.pay.model.enums.PayType.BALANCE_VOUCHER_PAY;
import static com.apihub.pay.utils.ApiDeductConstants.API_DEDUCT_BALANCE_KEY;
import static com.apihub.pay.utils.ApiDeductConstants.API_DEDUCT_TTL;

/**
 * @author IKUN
 * @description 针对表【pay_order(支付订单)】的数据库操作Service实现
 * @createDate 2023-11-02 11:58:00
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PayOrderServiceImpl extends ServiceImpl<PayOrderMapper, PayOrder>
        implements PayOrderService {

    private final UserServiceClient userServiceClient;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

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

        userServiceClient.chargeBalance(initPayOrder.getAmount());
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
            queryWrapper.le("amount", maxAmount);
        }
        if (minAmount != null) {
            if (maxAmount != null && maxAmount < minAmount) throw new BusinessException(ErrorCode.PARAMS_ERROR);
            queryWrapper.ge("amount", minAmount);
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


    //-----------------------------------------------------------------------------------------------------------------
    //voucherOrderRPC
    @Override
    public void payVoucherOrder(VoucherBalancePayDTO deductPayDTO) {
        VoucherOrderVO voucherOrderVO = deductPayDTO.getVoucherOrderVO();

        Integer amount = deductPayDTO.getAmount();
        if (amount == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "支付金额缺失");
        }

        if (!userServiceClient.deductBalance(amount)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "用户余额不足");
        }
        Long voucherId = voucherOrderVO.getVoucherId();
        Long voucherOrderVOId = voucherOrderVO.getId();

        PayOrder payOrder = new PayOrder();
        payOrder.setBizOrderNo(voucherOrderVOId);
        payOrder.setBizUserId(voucherOrderVO.getUserId());
        payOrder.setCreater(voucherOrderVO.getUserId());
        payOrder.setUpdater(UserHolder.getUser());
        payOrder.setAmount(amount);
        payOrder.setPaySuccessTime(new Date());
        payOrder.setCouponId(voucherId);
        payOrder.setStatus(BALANCE_VOUCHER_PAY.getValue());
        payOrder.setPayOverTime(new Date());
        payOrder.setExpandJson("");

        boolean b = this.save(payOrder);
        if (!b) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
    }

    @Override
    public void apiDeductByBalance(APIDeduct apiDeduct, HttpServletRequest request) {
        //数据检查
        if (apiDeduct.getInterfaceId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口id为空");
        }
        if (apiDeduct.getNum() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口id使用次数为空");
        }
        if (apiDeduct.getTotalFee() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "总费用为空");
        }
        String interfaceInfoIdStr = apiDeduct.getInterfaceId().toString();
        String apiNumStr = apiDeduct.getNum().toString();

        //查询redis中 用户调用次数
        String apiCountKey = API_DEDUCT_BALANCE_KEY + UserHolder.getUser();
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(apiCountKey);

        //没查询到,新建
        if (entries.isEmpty()) {
            Map<String, String> apiCount = new HashMap<>();
            apiCount.put(interfaceInfoIdStr, apiNumStr);
            stringRedisTemplate.opsForHash().putAll(apiCountKey, apiCount);
            stringRedisTemplate.expire(apiCountKey, API_DEDUCT_TTL, TimeUnit.HOURS);
            return;
        }

        //查询map中是否存在interfaceInfoId
        String interfaceCountString = (String) entries.get(interfaceInfoIdStr);
        //不存在,新增,并存储到redis中
        if (interfaceCountString == null) {
            stringRedisTemplate.opsForHash().put(
                    apiCountKey, interfaceInfoIdStr, apiNumStr);
            return;
        }

        Long interfaceCount = Long.valueOf(interfaceCountString);
        //存在，增加次数并更新到redis中
        interfaceCount += apiDeduct.getNum();
        entries.put(interfaceInfoIdStr, interfaceCount.toString());
        stringRedisTemplate.opsForHash().putAll(apiCountKey, entries);
    }

    @Override
    public Map<String, String> getTodayApiUsage(Long userId) {
        String apiCountKey = API_DEDUCT_BALANCE_KEY + userId;
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(apiCountKey);
        //将entries中的key从Object类型转换为String类型
        Map<String, String> entriesString = new HashMap<>();
        for (Map.Entry<Object, Object> entry : entries.entrySet()) {
            entriesString.put(entry.getKey().toString(), entry.getValue().toString());
        }
        return entriesString;
    }

}




