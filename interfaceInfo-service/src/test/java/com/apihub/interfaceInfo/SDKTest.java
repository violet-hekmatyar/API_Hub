package com.apihub.interfaceInfo;

import com.apihub.sdk.client.ApiHubIdClient;
import com.apihub.sdk.client.ApiHubSelfApiClient;
import com.apihub.sdk.exception.ApiException;
import com.apihub.sdk.model.dto.PictureQueryRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class SDKTest {

    @Resource
    private ApiHubIdClient apiHubIdClient;

    @Resource
    private ApiHubSelfApiClient apiHubSelfApiClient;

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

    @Test
    void PictureApi() {
        PictureQueryRequest pictureQueryRequest = new PictureQueryRequest();
        pictureQueryRequest.setSearchText("原神");
        pictureQueryRequest.setPageSize(15);
        pictureQueryRequest.setCurrent(4);
        try {
            System.out.println(apiHubSelfApiClient.PictureApi(pictureQueryRequest));
        } catch (ApiException e) {
            System.out.println(e.getMessage());
        }
    }
}
