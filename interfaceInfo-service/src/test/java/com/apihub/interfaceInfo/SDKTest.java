package com.apihub.interfaceInfo;

import com.apihub.sdk.client.ApiHubIdClient;
import com.apihub.sdk.exception.ApiException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class SDKTest {

    @Resource
    private ApiHubIdClient apiHubIdClient;

    @Test
    void SDKGetTest() {
        try {
            System.out.println(apiHubIdClient.interfaceIdByGet(1L, "format=text"));
        } catch (ApiException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void SDKPostTest() {
        try {
            System.out.println(apiHubIdClient.interfaceIdByPost(2L, "format=text"));
        } catch (ApiException e) {
            System.out.println(e.getMessage());
        }
    }
}
