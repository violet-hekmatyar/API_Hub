package com.apihub.sdk;


import com.apihub.sdk.client.ApiHubIdClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("apihub.client")
@Data
@ComponentScan
public class ApiHubClientConfig {

    private String accessKey;

    private String secretKey;

    @Bean
    public ApiHubIdClient apiHubIdClient() {
        return new ApiHubIdClient(accessKey, secretKey);
    }
}