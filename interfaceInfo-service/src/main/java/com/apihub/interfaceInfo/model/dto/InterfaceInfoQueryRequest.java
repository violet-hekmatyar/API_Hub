package com.apihub.interfaceInfo.model.dto;


import com.apihub.common.common.PageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询请求
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class InterfaceInfoQueryRequest extends PageRequest implements Serializable {

    /**
     * 主键
     */
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 接口地址
     */
    private String url;

    /**
     * 请求头
     */
    private String requestHeader;

    /**
     * 响应头
     */
    private String responseHeader;

    /**
     * 接口状态（0-关闭，1-开启）
     */
    private Integer status;

    /**
     * 请求类型
     */
    private String method;

    /**
     * 创建人
     */
    private Long userId;

    /**
     * 上限价格（分）
     */
    @ApiModelProperty(value = "接口上限价格", required = false)
    private Integer topPrice = 100;

    /**
     * 下限价格（分）
     */
    @ApiModelProperty(value = "接口下限价格", required = false)
    private Integer lowPrice = 0;

    /**
     * 类目名称
     */
    @ApiModelProperty(value = "接口所属类目", required = false)
    private String category;

}