package com.apihub.common.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Bean
    public RedissonClient redissonClient(){
        // 配置
        Config config = new Config();
        config.useSingleServer().setAddress("jdbc:redis://116.204.109.230:6379/6").setPassword("Jimmy@2333");
        // 创建RedissonClient对象
        return Redisson.create(config);
    }
}
