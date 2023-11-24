package com.apihub.voucher.service.impl;

import com.apihub.common.common.ErrorCode;
import com.apihub.common.exception.BusinessException;
import com.apihub.common.utils.UserHolder;
import com.apihub.voucher.mapper.VoucherInfoMapper;
import com.apihub.voucher.model.dto.*;
import com.apihub.voucher.model.entity.VoucherInfo;
import com.apihub.voucher.model.vo.VoucherInfoVO;
import com.apihub.voucher.openFeign.client.VoucherInfoClient;
import com.apihub.voucher.service.VoucherInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
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

    @Override
    public void delVoucherInfo(VoucherInfoDelRequest voucherInfoDelRequest) {
        //只有管理员才能添加
        if (!voucherInfoClient.checkAdmin()) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        Long id = voucherInfoDelRequest.getId();
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = this.removeById(id);
        if (!b) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
    }

    @Override
    public void updateVoucherInfo(VoucherInfoUpdateRequest voucherInfoUpdateRequest) {
        //只有管理员才能添加
        if (!voucherInfoClient.checkAdmin()) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        VoucherInfo updateVoucherInfo = new VoucherInfo();
        BeanUtils.copyProperties(voucherInfoUpdateRequest, updateVoucherInfo);
        boolean b = this.updateById(updateVoucherInfo);
        if (!b) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
    }

    @Override
    public List<VoucherInfo> listVoucherInfo(VoucherInfoListRequest voucherInfoListRequest) {
        if (!voucherInfoClient.checkAdmin()) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        VoucherInfo voucherInfoQuery = new VoucherInfo();
        Long payValueTop = null;
        Long payValueFloor = null;
        Long actualValueTop = null;
        Long actualValueFloor = null;

        if (voucherInfoListRequest != null) {
            BeanUtils.copyProperties(voucherInfoListRequest, voucherInfoQuery);

            payValueTop = voucherInfoListRequest.getPayValueTop();
            payValueFloor = voucherInfoListRequest.getPayValueFloor();
            actualValueTop = voucherInfoListRequest.getActualValueTop();
            actualValueFloor = voucherInfoListRequest.getActualValueFloor();
        }
        if (payValueTop == null) payValueTop = 99999999L;
        if (payValueFloor == null) payValueFloor = 0L;
        if (payValueTop < payValueFloor) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "payValue参数错误");
        }
        if (actualValueTop == null) actualValueTop = 99999999L;
        if (actualValueFloor == null) actualValueFloor = 0L;
        if (actualValueTop < actualValueFloor) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "actualValue参数错误");
        }

        QueryWrapper<VoucherInfo> voucherInfoQueryWrapper = new QueryWrapper<>(voucherInfoQuery);
        voucherInfoQueryWrapper.between("payValue", payValueFloor, payValueTop);
        voucherInfoQueryWrapper.between("actualValue", actualValueFloor, actualValueTop);


        return this.list(voucherInfoQueryWrapper);
    }

    @Override
    public Page<VoucherInfoVO> listVoucherInfoByPage(VoucherInfoListByPageRequest voucherInfoListByPageRequest) {
        VoucherInfo voucherInfoQuery = new VoucherInfo();
        long current = voucherInfoListByPageRequest.getCurrent();
        long pageSize = voucherInfoListByPageRequest.getPageSize();
        // 限制爬虫
        if (pageSize > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String sortOrder = voucherInfoListByPageRequest.getSortOrder();
        String sortField = voucherInfoListByPageRequest.getSortField();

        BeanUtils.copyProperties(voucherInfoListByPageRequest, voucherInfoQuery);

        Long payValueTop = voucherInfoListByPageRequest.getPayValueTop();
        Long payValueFloor = voucherInfoListByPageRequest.getPayValueFloor();
        Long actualValueTop = voucherInfoListByPageRequest.getActualValueTop();
        Long actualValueFloor = voucherInfoListByPageRequest.getActualValueFloor();
        if (payValueTop == null) payValueTop = 99999999L;
        if (payValueFloor == null) payValueFloor = 0L;
        if (payValueTop < payValueFloor) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "payValue参数错误");
        }
        if (actualValueTop == null) actualValueTop = 99999999L;
        if (actualValueFloor == null) actualValueFloor = 0L;
        if (actualValueTop < actualValueFloor) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "actualValue参数错误");
        }

        //需要模糊搜索
        String title = voucherInfoListByPageRequest.getTitle();
        String subTitle = voucherInfoListByPageRequest.getSubTitle();
        String rules = voucherInfoListByPageRequest.getRules();
        voucherInfoQuery.setTitle(null);
        voucherInfoQuery.setSubTitle(null);
        voucherInfoQuery.setRules(null);

        QueryWrapper<VoucherInfo> voucherInfoQueryWrapper = new QueryWrapper<>(voucherInfoQuery);
        voucherInfoQueryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        voucherInfoQueryWrapper.like(StringUtils.isNotBlank(title), "subTitle", subTitle);
        voucherInfoQueryWrapper.like(StringUtils.isNotBlank(title), "rules", rules);
        voucherInfoQueryWrapper.between("payValue", payValueFloor, payValueTop);
        voucherInfoQueryWrapper.between("actualValue", actualValueFloor, actualValueTop);
        voucherInfoQueryWrapper.orderBy(StringUtils.isNotBlank(sortField), sortOrder.equals("ascend"), sortField);

        Page<VoucherInfo> voucherInfoPage = this.page(new Page<>(current, pageSize), voucherInfoQueryWrapper);

        Page<VoucherInfoVO> voucherInfoVOPage = (Page<VoucherInfoVO>) voucherInfoPage.convert(voucherInfo -> {
            VoucherInfoVO voucherInfoVO = new VoucherInfoVO();
            BeanUtils.copyProperties(voucherInfo, voucherInfoVO);
            return voucherInfoVO;
        });
        return voucherInfoVOPage;
    }
}




