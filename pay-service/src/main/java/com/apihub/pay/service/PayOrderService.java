package com.apihub.pay.service;

import com.apihub.pay.model.dto.APIDeduct;
import com.apihub.pay.model.dto.ChargePayDTO;
import com.apihub.pay.model.dto.PayOrderQueryRequest;
import com.apihub.pay.model.dto.payvoucherorder.VoucherBalancePayDTO;
import com.apihub.pay.model.entity.PayOrder;
import com.apihub.pay.model.vo.PayOrderVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author IKUN
 * @description 针对表【pay_order(支付订单)】的数据库操作Service
 * @createDate 2023-11-02 11:58:00
 */
public interface PayOrderService extends IService<PayOrder> {

    void chargePayOrder(ChargePayDTO chargePayDTO);

    Page<PayOrderVO> listPayOrderByPage(PayOrderQueryRequest payOrderQueryRequest);

    void payVoucherOrder(VoucherBalancePayDTO deductPayDTO);

    void apiDeductByBalance(APIDeduct apiDeduct, HttpServletRequest request);

    Map<String, String> getTodayApiUsage(Long userId);
}
