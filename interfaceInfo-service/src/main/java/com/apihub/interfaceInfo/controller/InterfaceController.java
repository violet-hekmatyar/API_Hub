package com.apihub.interfaceInfo.controller;


import com.apihub.common.common.BaseResponse;
import com.apihub.common.common.ErrorCode;
import com.apihub.common.common.ResultUtils;
import com.apihub.common.exception.BusinessException;
import com.apihub.interfaceInfo.model.domain.InterfaceInfo;
import com.apihub.interfaceInfo.model.dto.InterfaceInfoAddRequest;
import com.apihub.interfaceInfo.service.InterfaceInfoService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/post")
@Slf4j
public class InterfaceController {

    @Resource
    private InterfaceInfoService interfaceInfoService;

    // region 增删改查
    @ApiOperation("用户添加接口")
    @PostMapping("/add/self")
    public BaseResponse<Boolean> addInterfaceInfo(@RequestBody InterfaceInfoAddRequest interfaceInfoAddRequest, HttpServletRequest request) {

        if (interfaceInfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoAddRequest, interfaceInfo);
        // 校验
        interfaceInfoService.validInterfaceInfo(interfaceInfo, true);
        //获取用户id，并且存储数据
        interfaceInfoService.saveInterfaceInfo(interfaceInfo, request);

        return ResultUtils.success(true);
    }

}
