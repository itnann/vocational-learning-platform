package com.yaojiuye.content.controller;

import com.yaojiuye.base.model.PageParams;
import com.yaojiuye.base.model.PageResult;
import com.yaojiuye.content.model.dto.QueryCourseParamsDto;
import com.yaojiuye.content.model.po.CourseBase;
import com.yaojiuye.content.service.ICourseBaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


/**
 * []
 *
 * @author : [itnan]
 * @version : [v1.0]
 * @createTime : [2025/3/5 17:36]
 */
@Api(tags = "课程信息编辑接口", value = "课程信息编辑接口")
@RestController
@RequiredArgsConstructor
public class CourseBaseInfoController {

    private final ICourseBaseService courseBaseService;

    @ApiOperation("课程查询接口")
    @PostMapping("/course/list")
    public PageResult<CourseBase> list(PageParams pageParams, @RequestBody(required = false) QueryCourseParamsDto queryCourseParams){
        return courseBaseService.queryCourseBaseList(pageParams, queryCourseParams);
    }
}
