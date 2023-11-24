package com.apihub.voucher.service;

import com.apihub.voucher.model.dto.VoucherInfoAddRequest;
import com.apihub.voucher.model.dto.VoucherInfoDelRequest;
import com.apihub.voucher.model.dto.VoucherInfoListRequest;
import com.apihub.voucher.model.dto.VoucherInfoUpdateRequest;
import com.apihub.voucher.model.entity.VoucherInfo;
import com.apihub.voucher.model.vo.VoucherInfoVO;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author IKUN
 * @description 针对表【voucher_info】的数据库操作Service
 * @createDate 2023-11-22 20:48:15
 */
public interface VoucherInfoService extends IService<VoucherInfo> {

    void saveVoucherInfo(VoucherInfoAddRequest voucherInfoAddRequest, HttpServletRequest request);

    void delVoucherInfo(VoucherInfoDelRequest voucherInfoDelRequest);

    void updateVoucherInfo(VoucherInfoUpdateRequest voucherInfoUpdateRequest);

    List<VoucherInfoVO> listVoucherInfo(VoucherInfoListRequest voucherInfoListRequest);
}
