package com.apihub.interfaceInfo;

import com.apihub.sdk.client.ApiHubIdClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class SDKTest {

    @Resource
    private ApiHubIdClient apiHubIdClient;

    @Test
    void SDKGetTest() {
        System.out.println(apiHubIdClient.interfaceIdByGet(1L, "format=text"));

    }
}
