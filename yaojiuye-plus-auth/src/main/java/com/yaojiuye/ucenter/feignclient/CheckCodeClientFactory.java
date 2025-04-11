package com.yaojiuye.ucenter.feignclient;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author itnan
 * @ClassName CheckCodeClientFactory
 * @Description 远程调用验证码服务降级策略
 * @Date 2025/4/11 18:22
 * @Version V1.0
 */
@Component
@Slf4j
public class CheckCodeClientFactory implements FallbackFactory<CheckCodeClient> {

    @Override
    public CheckCodeClient create(Throwable throwable) {
        return (String key, String code) -> {
            log.debug("调用验证码服务发生熔断走降级方法,验证码key: {} ,验证码code: {} ,熔断异常:{}", key, code, throwable.getMessage());
            return false;
        };

    }
}
