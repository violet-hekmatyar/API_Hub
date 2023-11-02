package com.apihub.pay;


import com.apihub.pay.openFeign.config.DefaultFeignConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@EnableFeignClients(defaultConfiguration = DefaultFeignConfig.class)
@MapperScan("com.apihub.pay.mapper")
@SpringBootApplication
public class payServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(payServiceApplication.class, args);
    }
}
