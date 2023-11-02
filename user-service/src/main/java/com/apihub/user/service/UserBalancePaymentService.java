package com.apihub.user.service;

import com.apihub.user.model.entity.UserBalancePayment;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author IKUN
* @description 针对表【user_balance_payment(用户余额表)】的数据库操作Service
* @createDate 2023-11-02 10:29:07
*/
public interface UserBalancePaymentService extends IService<UserBalancePayment> {

    Boolean deductBalance(Integer amount);

    Boolean chargeBalance(Integer amount);
}
