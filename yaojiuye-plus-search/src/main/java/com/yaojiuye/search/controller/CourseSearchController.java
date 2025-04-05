package com.yaojiuye.search.controller;

import com.yaojiuye.base.model.PageParams;
import com.yaojiuye.search.dto.SearchCourseParamDto;
import com.yaojiuye.search.dto.SearchPageResultDto;
import com.yaojiuye.search.po.CourseIndex;
import com.yaojiuye.search.service.CourseSearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author itnan
 * @version 1.0
 * @description 课程搜索接口
 * @date 2022/9/24 22:31
 */
@Api(value = "课程搜索接口", tags = "课程搜索接口")
@RestController
@RequestMapping("/course")
public class CourseSearchController {

    @Autowired
    CourseSearchService courseSearchService;


    @ApiOperation("课程搜索列表")
    @GetMapping("/list")
    public SearchPageResultDto<CourseIndex> list(PageParams pageParams, SearchCourseParamDto searchCourseParamDto) {

        return courseSearchService.queryCoursePubIndex(pageParams, searchCourseParamDto);

    }
}
