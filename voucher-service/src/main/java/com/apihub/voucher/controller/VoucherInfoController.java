package com.apihub.voucher.controller;

import com.apihub.common.common.BaseResponse;
import com.apihub.common.common.ResultUtils;
import com.apihub.voucher.model.dto.*;
import com.apihub.voucher.model.entity.VoucherInfo;
import com.apihub.voucher.model.vo.VoucherInfoVO;
import com.apihub.voucher.service.VoucherInfoService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/voucherInfo")

public class VoucherInfoController {
    @Resource
    private VoucherInfoService voucherInfoService;


    @ApiOperation("添加优惠券信息")
    @PostMapping("/add")
    public BaseResponse<Boolean> addVoucherInfo(@RequestBody VoucherInfoAddRequest voucherInfoAddRequest,
                                                HttpServletRequest request) {

        voucherInfoService.saveVoucherInfo(voucherInfoAddRequest, request);
        return ResultUtils.success(true);
    }

    //todo 对着添加样例，写出CRUD
    //同时写出 分页查询

    @ApiOperation("删除优惠券信息")
    @PostMapping("/del")
    public BaseResponse<Boolean> delVoucherInfo(@RequestBody VoucherInfoDelRequest voucherInfoDelRequest,
                                                HttpServletRequest request) {

        voucherInfoService.delVoucherInfo(voucherInfoDelRequest);
        return ResultUtils.success(true);
    }

    @ApiOperation("修改优惠券信息")
    @PostMapping("/update")
    public BaseResponse<Boolean> updateVoucherInfo(@RequestBody VoucherInfoUpdateRequest voucherInfoUpdateRequest,
                                                   HttpServletRequest request) {

        voucherInfoService.updateVoucherInfo(voucherInfoUpdateRequest);
        return ResultUtils.success(true);
    }


    @ApiOperation("全局搜索优惠券信息（管理员）")
    @PostMapping("/list/admin")
    public BaseResponse<List<VoucherInfo>> listVoucherInfo(@RequestBody VoucherInfoListRequest voucherInfoListRequest,
                                                           HttpServletRequest request) {

        return ResultUtils.success(voucherInfoService.listVoucherInfo(voucherInfoListRequest));
    }

    @ApiOperation("分页搜索优惠券信息")
    @PostMapping("/list/page")
    public BaseResponse<Page<VoucherInfoVO>> listVoucherInfoByPage(@RequestBody VoucherInfoListByPageRequest voucherInfoListByPageRequest,
                                                                   HttpServletRequest request) {

        return ResultUtils.success(voucherInfoService.listVoucherInfoByPage(voucherInfoListByPageRequest));
    }
}
