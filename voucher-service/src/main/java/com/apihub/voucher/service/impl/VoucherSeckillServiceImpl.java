package com.apihub.voucher.service.impl;

import com.apihub.common.common.BaseResponse;
import com.apihub.common.utils.UserHolder;
import com.apihub.voucher.mapper.VoucherSeckillMapper;
import com.apihub.voucher.model.entity.VoucherSeckill;
import com.apihub.voucher.model.vo.VoucherSeckillVO;
import com.apihub.voucher.service.VoucherSeckillService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author IKUN
 * @description 针对表【voucher_seckill(秒杀优惠券表)】的数据库操作Service实现
 * @createDate 2023-11-22 20:48:34
 */
@Service
public class VoucherSeckillServiceImpl extends ServiceImpl<VoucherSeckillMapper, VoucherSeckill>
        implements VoucherSeckillService {

    @Override
    public BaseResponse<VoucherSeckillVO> seckillVoucher(Long voucherId) {

        //todo 完成秒杀功能
        return createVoucherOrder(voucherId);
    }

    //单纯创建订单
    public BaseResponse<VoucherSeckillVO> createVoucherOrder(Long voucherId) {
        Long userId = UserHolder.getUser();

        return null;
    }
}




