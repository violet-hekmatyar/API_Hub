package com.apihub.user.service.impl;

import com.apihub.common.common.ErrorCode;
import com.apihub.common.exception.BusinessException;
import com.apihub.common.utils.BeanUtils;
import com.apihub.common.utils.UserHolder;
import com.apihub.user.mapper.UserBalancePaymentMapper;
import com.apihub.user.model.entity.UserBalancePayment;
import com.apihub.user.model.vo.UserBalanceVO;
import com.apihub.user.service.UserBalancePaymentService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author IKUN
 * @description 针对表【user_balance_payment(用户余额表)】的数据库操作Service实现
 * @createDate 2023-11-02 10:29:07
 */
@Slf4j
@Service
public class UserBalancePaymentServiceImpl extends ServiceImpl<UserBalancePaymentMapper, UserBalancePayment>
        implements UserBalancePaymentService {

    @Resource
    private UserBalancePaymentMapper userBalancePaymentMapper;

    @Override
    public Boolean deductBalance(Integer amount) {
        log.info("开始扣款");
        QueryWrapper<UserBalancePayment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", UserHolder.getUser());
        UserBalancePayment userBalancePayment = userBalancePaymentMapper.selectOne(queryWrapper);
        if (userBalancePayment.getUserId() == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "未查询到用户余额信息");
        }
        if (userBalancePayment.getBalance() < amount) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "余额不足");
        }
        //扣减余额
        userBalancePayment.setBalance(userBalancePayment.getBalance() - amount);
        return this.updateById(userBalancePayment);
    }

    @Override
    public Boolean chargeBalance(Integer amount) {
        log.info("开始充值,充值用户Id：" + UserHolder.getUser() + "充值金额：" + amount);
        if (UserHolder.getUser() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        QueryWrapper<UserBalancePayment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", UserHolder.getUser());
        UserBalancePayment userBalancePayment = userBalancePaymentMapper.selectOne(queryWrapper);
        if (userBalancePayment.getUserId() == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "未查询到用户余额信息");
        }
        //扣减余额
        userBalancePayment.setBalance(userBalancePayment.getBalance() + amount);
        return this.updateById(userBalancePayment);
    }

    @Override
    public UserBalanceVO getBalance(HttpServletRequest request) {
        QueryWrapper<UserBalancePayment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", UserHolder.getUser());
        UserBalancePayment userBalancePayment = userBalancePaymentMapper.selectOne(queryWrapper);
        if (userBalancePayment.getUserId() == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "未查询到用户余额信息");
        }
        return BeanUtils.toBean(userBalancePayment, UserBalanceVO.class);
    }
}




