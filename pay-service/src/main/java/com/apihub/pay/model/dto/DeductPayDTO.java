package com.apihub.pay.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "支付下单表单实体")
public class DeductPayDTO {
    @ApiModelProperty(value = "业务订单id",required = true)
    @NotNull(message = "业务订单id不能为空")
    private Long bizOrderNo;

    @ApiModelProperty(value = "支付金额",required = true)
    @Min(value = 1, message = "支付金额必须为正数")
    private Integer amount;

    @ApiModelProperty(value = "支付方式",required = true)
    @NotNull(message = "支付方式不能为空")
    private Integer payType;

    @ApiModelProperty(value = "接口价格",required = true)
    @NotNull(message = "接口价格不能为空")
    private String interfacePrice;
}