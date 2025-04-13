package com.yaojiuye.content;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yaojiuye.base.model.PageParams;
import com.yaojiuye.base.model.PageResult;
import com.yaojiuye.content.mapper.CourseBaseMapper;
import com.yaojiuye.content.model.dto.QueryCourseParamsDto;
import com.yaojiuye.content.model.po.CourseBase;
import com.yaojiuye.content.service.ICourseBaseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


/**
 * []
 *
 * @author : [itnan]
 * @version : [v1.0]
 * @createTime : [2025/3/7 10:18]
 */

@SpringBootTest
@Slf4j
public class CourseBaseServiceTests {

    @Autowired
    private ICourseBaseService courseBaseService;

    @Test
    public void testCourseBaseMapper(){
        QueryCourseParamsDto queryCourseParamsDto = new QueryCourseParamsDto();
        queryCourseParamsDto.setCourseName("java");
        queryCourseParamsDto.setAuditStatus("202004");
        PageResult<CourseBase> courseBasePageResult = courseBaseService.queryCourseBaseList(1_232_141_425L,new PageParams(), queryCourseParamsDto);
        Assertions.assertNotNull(courseBasePageResult);

    }
}
