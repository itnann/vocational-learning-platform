package com.yaojiuye.learning;

import com.yaojiuye.content.model.po.CoursePublish;
import com.yaojiuye.learning.feignclient.ContentServiceClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author itnan
 * @version 1.0
 * @description TODO
 */
@SpringBootTest
public class FeignClientTest {

    @Autowired
    ContentServiceClient contentServiceClient;


    @Test
    public void testContentServiceClient() {
        CoursePublish coursepublish = contentServiceClient.getCoursepublish(139L);
        Assertions.assertNotNull(coursepublish);
    }
}