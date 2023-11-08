package com.apihub.api.model.dto;


import lombok.Data;

@Data
public class DeductOrderDTO {

    /**
     * 总金额，单位为分
     */
    private Integer totalFee;

    /**
     * 支付类型，1、支付宝，2、微信，3、扣减余额
     */
    private Integer paymentType;

    /**
     * 接口id
     */
    private Long interfaceId;

    /**
     * 使用次数
     */
    private Long num;

    /**
     * 用户的id地址
     */
    private String userAddress;
}
