package com.yaojiuye.content.feignclient;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author itnan
 * @ClassName MediaServiceClientFallbackFactory
 * @Description 熔断降级处理
 * @Date 2025/4/4 21:15
 * @Version V1.0
 */
@Component
@Slf4j
public class MediaServiceClientFallbackFactory implements FallbackFactory<MediaServiceClient> {
    @Override
    public MediaServiceClient create(Throwable throwable) {

        /*return new MediaServiceClient() {
            @Override
            public String uploadFile(MultipartFile upload, String objectName) {
                return null;
            }
        };
    }*/
        return (MultipartFile upload, String objectName, String companyIdFeign) -> {
            log.error("远程调用媒资管理服务上传文件出错", throwable.toString(), throwable);
            return null;
        };
    }
}
