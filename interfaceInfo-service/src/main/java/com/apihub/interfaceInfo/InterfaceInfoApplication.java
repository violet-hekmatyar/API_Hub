package com.apihub.interfaceInfo;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.apihub.interfaceInfo.mapper")
@SpringBootApplication
public class InterfaceInfoApplication {
    public static void main(String[] args) {
        SpringApplication.run(InterfaceInfoApplication.class, args);
    }
}
