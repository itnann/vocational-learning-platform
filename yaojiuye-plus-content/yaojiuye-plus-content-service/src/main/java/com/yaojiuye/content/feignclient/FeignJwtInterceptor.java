package com.yaojiuye.content.feignclient;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Component;

/**
 * @author itnan
 * @ClassName FeignJwtInterceptor
 * @Description 请求拦截器
 * @Date 2025/4/13 2:00
 * @Version V1.0
 */
@Component
public class FeignJwtInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        // 获取当前认证信息
        OAuth2Authentication authentication =
                (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            // 提取访问令牌
            OAuth2AuthenticationDetails details =
                    (OAuth2AuthenticationDetails) authentication.getDetails();
            String token = details.getTokenValue();

            // 将令牌添加到请求头
            template.header("Authorization", "Bearer " + token);
        }
    }
}
