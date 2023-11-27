package com.apihub.voucher.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ApiTokenVO implements Serializable {

    String token;

    Long interfaceId;
}
