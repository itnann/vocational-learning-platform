package com.yaojiuye.media.config;

import io.minio.MinioClient;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author itnan
 * @ClassName MinioConfig
 * @Description minio分布式文件系统配置
 * @Date 2025/3/20 16:31
 * @Version V1.0
 */
@Configuration
@ConfigurationProperties(prefix = "minio")
@Data//ConfigurationProperties需要构建set方法进行反射注入
public class MinioConfig {

    //@Value("${minio.endpoint}")
    private String endpoint;
    //@Value("${minio.accessKey}")
    private String accessKey;
    //@Value("${minio.secretKey}")
    private String secretKey;

    @Bean
    public MinioClient minioClient() {

        MinioClient minioClient =
                MinioClient.builder()
                        .endpoint(endpoint)
                        .credentials(accessKey, secretKey)
                        .build();
        return minioClient;
    }
}
