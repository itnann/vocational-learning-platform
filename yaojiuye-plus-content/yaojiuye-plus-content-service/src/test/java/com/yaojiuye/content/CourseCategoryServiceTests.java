package com.yaojiuye.content;

import com.yaojiuye.base.model.PageParams;
import com.yaojiuye.base.model.PageResult;
import com.yaojiuye.content.model.dto.CourseCategoryTreeDto;
import com.yaojiuye.content.model.dto.QueryCourseParamsDto;
import com.yaojiuye.content.model.po.CourseBase;
import com.yaojiuye.content.service.ICourseCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;


/**
 * []
 *
 * @author : [itnan]
 * @version : [v1.0]
 * @createTime : [2025/3/7 10:18]
 */

@SpringBootTest
@Slf4j
public class CourseCategoryServiceTests {

    @Autowired
    private ICourseCategoryService courseCategoryService;

    @Test
    public void testCourseCategoryMapper(){
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryService.queryTreeNodes("1");
        courseCategoryTreeDtos.forEach(item->{
            log.info("{}",item);
        });

    }
}
