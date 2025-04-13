package com.yaojiuye.content;

import com.yaojiuye.content.config.MultipartSupportConfig;
import com.yaojiuye.content.feignclient.MediaServiceClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * @author itnan
 * @ClassName FeignUploadTest
 * @Description 测试使用feign远程上传文件
 * @Date 2025/4/4 18:14
 * @Version V1.0
 */
@SpringBootTest
public class FeignUploadTest {

    @Autowired
    MediaServiceClient mediaServiceClient;

    //远程调用，上传文件
    @Test
    public void test() {

        MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(new File("G:/code_tzn/upload/139.html"));
        mediaServiceClient.uploadFile(multipartFile,"course/139.html", "1232141425");
    }

}
