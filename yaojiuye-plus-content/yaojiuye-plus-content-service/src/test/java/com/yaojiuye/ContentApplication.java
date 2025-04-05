package com.yaojiuye;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * [内容管理服务启动类]
 *
 * @author : [itnan]
 * @version : [v1.0]
 * @createTime : [2025/3/5 17:47]
 */
@SpringBootApplication
@Slf4j
@EnableFeignClients(basePackages={"com.yaojiuye.content.feignclient"}) // 扫描feignClient
public class ContentApplication {
    public static void main(String[] args) {
        SpringApplication.run(ContentApplication.class, args);
        log.info("Content server_test started");
    }
}