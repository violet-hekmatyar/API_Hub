package com.apihub.pay.service.impl;

import com.apihub.common.common.ErrorCode;
import com.apihub.common.exception.BusinessException;
import com.apihub.common.utils.UserHolder;
import com.apihub.pay.mapper.ApiOrderMapper;
import com.apihub.pay.model.dto.DeductOrderDTO;
import com.apihub.pay.model.entity.ApiOrder;
import com.apihub.pay.service.ApiOrderService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Objects;

/**
 * @author IKUN
 * @description 针对表【api_order】的数据库操作Service实现
 * @createDate 2023-11-07 13:15:27
 */
@Service
public class ApiOrderServiceImpl extends ServiceImpl<ApiOrderMapper, ApiOrder>
        implements ApiOrderService {

    @Resource
    private ApiOrderMapper apiOrderMapper;

    @Override
    public Boolean deductOrder(DeductOrderDTO deductOrderDTO) {
        //数据检查
        if (deductOrderDTO.getInterfaceId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口id为空");
        }
        if (deductOrderDTO.getNum() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口id使用次数为空");
        }
        if (!Objects.equals(UserHolder.getUser(), deductOrderDTO.getUserId())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不正确");
        }
        if (deductOrderDTO.getTotalFee() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "总费用为空");
        }
        //查询订单
        QueryWrapper<ApiOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", UserHolder.getUser());
        queryWrapper.eq("interfaceId", deductOrderDTO.getInterfaceId());

        Calendar newCalendar = Calendar.getInstance();
        //todo 下面筛选endTime不知道为啥不行

        //queryWrapper.le("endTime",newCalendar.getTime());
        //直接选择今天的日期，指定时间，直接通过string比较
        newCalendar.set(Calendar.MINUTE, 59);
        newCalendar.set(Calendar.HOUR_OF_DAY, 23);
        newCalendar.set(Calendar.SECOND, 0);
        queryWrapper.eq("otherInfo", newCalendar.getTime().toString());

        ApiOrder oldOrder = apiOrderMapper.selectOne(queryWrapper);
        //查询到订单，修改数据
        if (oldOrder != null) {
            oldOrder.setTotalFee(oldOrder.getTotalFee() + deductOrderDTO.getTotalFee());
            oldOrder.setNum(oldOrder.getNum() + deductOrderDTO.getNum());
            return this.updateById(oldOrder);
        }
        //没查询到订单，新建订单
        ApiOrder newOrder = new ApiOrder();
        newOrder.setInterfaceId(deductOrderDTO.getInterfaceId());
        newOrder.setUserId(deductOrderDTO.getUserId());
        newOrder.setNum(deductOrderDTO.getNum());
        newOrder.setPaymentType(deductOrderDTO.getPaymentType());
        newOrder.setTotalFee(deductOrderDTO.getTotalFee());

        newOrder.setStatus(2);


        //结束时间设置为当天的23:59:59
        newOrder.setEndTime(newCalendar.getTime());
        //选择今天的日期，指定时间，以string形式存储
        newOrder.setOtherInfo(newCalendar.getTime().toString());

        newOrder.setPaymentType(3);
        //todo 用户ip
        newOrder.setUserAddress("127.0.0.1");

        return this.save(newOrder);
    }
}




