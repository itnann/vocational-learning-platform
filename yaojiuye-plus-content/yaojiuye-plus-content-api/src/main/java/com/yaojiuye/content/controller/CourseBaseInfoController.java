package com.yaojiuye.content.controller;

import com.yaojiuye.base.exception.ValidationGroups;
import com.yaojiuye.base.model.PageParams;
import com.yaojiuye.base.model.PageResult;
import com.yaojiuye.content.model.dto.AddCourseDto;
import com.yaojiuye.content.model.dto.CourseBaseInfoDto;
import com.yaojiuye.content.model.dto.EditCourseDto;
import com.yaojiuye.content.model.dto.QueryCourseParamsDto;
import com.yaojiuye.content.model.po.CourseBase;
import com.yaojiuye.content.service.ICourseBaseService;
import com.yaojiuye.content.util.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


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

    @ApiOperation("课程分页查询接口")
    @PostMapping("/course/list")
    public PageResult<CourseBase> list(PageParams pageParams, @RequestBody(required = false) QueryCourseParamsDto queryCourseParams){
        return courseBaseService.queryCourseBaseList(pageParams, queryCourseParams);
    }

    @ApiOperation("新增课程基本信息")
    @PostMapping("/course")
    public CourseBaseInfoDto createCourseBase(@RequestBody @Validated(ValidationGroups.Inster.class) AddCourseDto addCourseDto){
        //机构id，由于认证系统没有上线暂时硬编码
        //TODO 获取当前机构id
        Long companyId = 1232141425L;
        return courseBaseService.createCourseBase(companyId,addCourseDto);
    }

    @ApiOperation("根据课程id查询基本信息")
    @GetMapping("/course/{courseId}")
    public CourseBaseInfoDto getCourseBaseById(@PathVariable Long courseId){
        return courseBaseService.getCourseBaseInfo(courseId);
    }

    @ApiOperation("修改课程基本信息")
    @PutMapping("/course")
    public CourseBaseInfoDto modifyCourseBase(@RequestBody @Validated(ValidationGroups.Update.class)EditCourseDto editCourseDto){
        //机构id，由于认证系统没有上线暂时硬编码
        //TODO 获取当前机构id
        Long companyId = 1232141425L;
        return courseBaseService.updateCourseBase(companyId,editCourseDto);
    }

    @ApiOperation("删除课程")
    @DeleteMapping("/course/{courseId}")
    public void deleteCourseBase(@PathVariable Long courseId){
        courseBaseService.deleteCourseBase(courseId);
    }


}
