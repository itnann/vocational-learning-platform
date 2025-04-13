package com.yaojiuye.content;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yaojiuye.base.model.PageParams;
import com.yaojiuye.base.model.PageResult;
import com.yaojiuye.content.mapper.CourseBaseMapper;
import com.yaojiuye.content.model.dto.QueryCourseParamsDto;
import com.yaojiuye.content.model.po.CourseBase;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.function.Function;


/**
 * []
 *
 * @author : [itnan]
 * @version : [v1.0]
 * @createTime : [2025/3/7 10:18]
 */

@SpringBootTest
@Slf4j
public class CourseBaseMapperTests {

    @Autowired
    private CourseBaseMapper coursebaseMapper;

    @Test
    public void testCourseBaseMapper(){
        CourseBase courseBase = coursebaseMapper.selectById(18L);
        Assertions.assertNotNull(courseBase);

        QueryCourseParamsDto queryCourseParamsDto = new QueryCourseParamsDto();
        queryCourseParamsDto.setCourseName("java");


        QueryWrapper<CourseBase> wrapper = new QueryWrapper<CourseBase>();
        wrapper.lambda().like(StringUtils.isNotEmpty(queryCourseParamsDto.getCourseName()), CourseBase::getName, queryCourseParamsDto.getCourseName())
                .eq(StringUtils.isNotEmpty(queryCourseParamsDto.getAuditStatus()), CourseBase::getAuditStatus, queryCourseParamsDto.getAuditStatus());

        PageParams pageParams = new PageParams();
        Page<CourseBase> page = pageParams.toMpPage();
        Page<CourseBase> courseBasePage = coursebaseMapper.selectPage(page, wrapper);
        PageResult<CourseBase> pageResult = PageResult.po2Po(courseBasePage);
        log.info("pageResult:{}",pageResult);

    }
}
