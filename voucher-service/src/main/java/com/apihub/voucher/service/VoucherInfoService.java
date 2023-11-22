package com.apihub.voucher.service;

import com.apihub.voucher.model.dto.VoucherInfoAddRequest;
import com.apihub.voucher.model.entity.VoucherInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
 * @author IKUN
 * @description 针对表【voucher_info】的数据库操作Service
 * @createDate 2023-11-22 20:48:15
 */
public interface VoucherInfoService extends IService<VoucherInfo> {

    void saveVoucherInfo(VoucherInfoAddRequest voucherInfoAddRequest, HttpServletRequest request);
}
