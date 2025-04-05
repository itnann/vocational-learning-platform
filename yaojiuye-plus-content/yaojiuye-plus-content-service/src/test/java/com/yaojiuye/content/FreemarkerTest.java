package com.yaojiuye.content;

import com.yaojiuye.content.model.dto.CoursePreviewDto;
import com.yaojiuye.content.service.CoursePublishService;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import freemarker.template.Configuration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;

/**
 * @author itnan
 * @ClassName FreemarkerTest
 * @Description 测试页面静态化
 * @Date 2025/4/4 17:01
 * @Version V1.0
 */
@SpringBootTest
public class FreemarkerTest {

    @Autowired
    private CoursePublishService coursePublishService;

    @Test
    public void testFreemarker() throws Exception {
        //配置freemarker
        Configuration configuration = new Configuration(Configuration.getVersion());
        //加载模板
        //选指定模板路径,classpath下templates下
        //得到classpath路径
        //String classpath = this.getClass().getResource("/").getPath();
        String classpath = this.getClass().getClassLoader().getResource("").getPath();
        configuration.setDirectoryForTemplateLoading(new File(classpath + "/templates/"));
        //设置字符集
        configuration.setDefaultEncoding("utf-8");
        //指定模板文件名称
        Template template = configuration.getTemplate("course_template.ftl");

        CoursePreviewDto coursePreviewInfo = coursePublishService.getCoursePreviewInfo(139L);
        HashMap<String, Object> map = new HashMap<>();
        map.put("model", coursePreviewInfo);

        String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);

        InputStream inputStream = IOUtils.toInputStream(html, "UTF-8");
        FileOutputStream outputStream = new FileOutputStream(new File("G:/code_tzn/upload/139.html"));
        IOUtils.copy(inputStream, outputStream);
    }
}
