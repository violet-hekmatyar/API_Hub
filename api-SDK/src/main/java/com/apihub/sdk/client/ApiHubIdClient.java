package com.apihub.sdk.client;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;

import java.util.HashMap;
import java.util.Map;

import static com.apihub.sdk.utils.ApiSignUtils.genSign;

public class ApiHubIdClient {

    private static final String GATEWAY_HOST = "http://localhost:8100/api";

    private final String accessKey;

    private final String secretKey;

    public ApiHubIdClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    private Map<String, String> getHeaderMap(String body) {
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("accessKey", accessKey);
        // 一定不能直接发送
//        hashMap.put("secretKey", secretKey);
        hashMap.put("body", body);
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        hashMap.put("timestamp", timestamp);
        hashMap.put("secretSign", genSign(timestamp, secretKey));
        return hashMap;
    }


    public String interfaceIdByGet(Long interfaceId, String body) {
        //数据拼接
        String interfaceIdURL = URLUtil.decode(String.valueOf(interfaceId), CharsetUtil.CHARSET_UTF_8);
        String requestBody = URLUtil.decode(body, CharsetUtil.CHARSET_UTF_8);

        HttpResponse httpResponse = HttpRequest.get(GATEWAY_HOST + "/apiService/get" +
                        "?InterfaceId=" + interfaceIdURL + "&body=" + requestBody)
                .addHeaders(getHeaderMap(""))
                .execute();
        System.out.println(httpResponse.getStatus());
        String result = httpResponse.body();
        System.out.println(result);
        return result;
    }

    public String interfaceIdByPost(Long interfaceId, String body) {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("InterfaceId", interfaceId);
        paramMap.put("body", body);
        String json = JSONUtil.toJsonStr(body);
        HttpResponse httpResponse = HttpRequest.post(GATEWAY_HOST + "/apiService/post")
                .addHeaders(getHeaderMap(json))
                .body(json)
                .execute();
        System.out.println(httpResponse.getStatus());
        String result = httpResponse.body();
        System.out.println(result);
        return result;
    }
}
