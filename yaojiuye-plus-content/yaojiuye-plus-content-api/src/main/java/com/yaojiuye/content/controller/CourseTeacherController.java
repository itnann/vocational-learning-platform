package com.yaojiuye.content.controller;

import com.yaojiuye.base.exception.ValidationGroups;
import com.yaojiuye.content.model.po.CourseTeacher;
import com.yaojiuye.content.service.ICourseTeacherService;
import com.yaojiuye.content.util.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author itnan
 * @ClassName CourseTeacherController
 * @Description 课程老师
 * @Date 2025/3/16 18:07
 * @Version V1.0
 */
@Api(tags = "课程老师")
@RestController
@RequiredArgsConstructor
public class CourseTeacherController {

    private final ICourseTeacherService courseTeacherService;

    @ApiOperation("查询课程老师")
    @GetMapping("/courseTeacher/list/{courseId}")
    public List<CourseTeacher> list(@PathVariable Long courseId) {
        return courseTeacherService.lambdaQuery().eq(CourseTeacher::getCourseId, courseId).list();
    }

    @ApiOperation("添加课程老师")
    @PostMapping("/courseTeacher")
    public CourseTeacher saveOrUpdate(@RequestBody  @Validated CourseTeacher courseTeacher) {
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        Long companyId = null;
        if(StringUtils.isNotEmpty(user.getCompanyId())){
            companyId = Long.valueOf(user.getCompanyId());
        }
        return courseTeacherService.saveOrUpdateCourseTeacher(companyId, courseTeacher);
    }

    @ApiOperation("删除课程老师")
    @DeleteMapping("/courseTeacher/{id}")
    public void delete(@PathVariable Long id) {
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        Long companyId = null;
        if(StringUtils.isNotEmpty(user.getCompanyId())){
            companyId = Long.valueOf(user.getCompanyId());
        }
        courseTeacherService.deleteCourseTeacher(companyId, id);
    }

}
