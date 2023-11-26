package com.apihub.voucher.service;

import com.apihub.common.common.BaseResponse;
import com.apihub.voucher.model.entity.VoucherSeckill;
import com.apihub.voucher.model.vo.VoucherSeckillVO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author IKUN
 * @description 针对表【voucher_seckill(秒杀优惠券表)】的数据库操作Service
 * @createDate 2023-11-22 20:48:34
 */
public interface VoucherSeckillService extends IService<VoucherSeckill> {

    BaseResponse<VoucherSeckillVO> seckillVoucher(Long voucherId);
}
