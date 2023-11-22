package com.apihub.voucher.service.impl;

import com.apihub.common.common.ErrorCode;
import com.apihub.common.exception.BusinessException;
import com.apihub.common.utils.UserHolder;
import com.apihub.voucher.mapper.VoucherInfoMapper;
import com.apihub.voucher.model.dto.VoucherInfoAddRequest;
import com.apihub.voucher.model.entity.VoucherInfo;
import com.apihub.voucher.openFeign.client.VoucherInfoClient;
import com.apihub.voucher.service.VoucherInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

import static com.apihub.voucher.utils.VoucherInfoConstant.STATUS_DOWN;

/**
 * @author IKUN
 * @description 针对表【voucher_info】的数据库操作Service实现
 * @createDate 2023-11-22 20:48:15
 */
@Service
@RequiredArgsConstructor
public class VoucherInfoServiceImpl extends ServiceImpl<VoucherInfoMapper, VoucherInfo>
        implements VoucherInfoService {
    private final VoucherInfoClient voucherInfoClient;

    @Override
    public void saveVoucherInfo(VoucherInfoAddRequest voucherInfoAddRequest, HttpServletRequest request) {
        //只有管理员才能添加
        if (!voucherInfoClient.checkAdmin()) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "不是管理员");
        }
        //数据校验
        if (voucherInfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String title = voucherInfoAddRequest.getTitle();
        if (StringUtils.isAnyBlank(title)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (title.length() > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "优惠券标题过长");
        }
        Long payValue = voucherInfoAddRequest.getPayValue();
        Long actualValue = voucherInfoAddRequest.getActualValue();
        if (payValue == null || actualValue == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (payValue >= actualValue) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "优惠券无优惠额度");
        }
        VoucherInfo voucherInfoSave = new VoucherInfo();
        BeanUtils.copyProperties(voucherInfoAddRequest, voucherInfoSave);

        //如果没有设置优惠券提供者，则默认为设置人员
        if (voucherInfoSave.getIssuerId() == null) voucherInfoSave.setIssuerId(UserHolder.getUser());

        voucherInfoSave.setStatus(STATUS_DOWN);

        boolean save = this.save(voucherInfoSave);
        if (!save) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
    }
}




