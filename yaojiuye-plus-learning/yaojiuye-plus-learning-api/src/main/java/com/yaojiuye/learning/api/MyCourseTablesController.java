package com.yaojiuye.learning.api;

import com.yaojiuye.base.exception.GlobalException;
import com.yaojiuye.base.model.PageResult;
import com.yaojiuye.learning.model.dto.MyCourseTableParams;
import com.yaojiuye.learning.model.dto.XcChooseCourseDto;
import com.yaojiuye.learning.model.dto.XcCourseTablesDto;
import com.yaojiuye.learning.model.po.XcCourseTables;
import com.yaojiuye.learning.service.MyCourseTablesService;
import com.yaojiuye.learning.util.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author itnan
 * @version 1.0
 * @description 我的课程表接口
 */

@Api(value = "我的课程表接口", tags = "我的课程表接口")
@Slf4j
@RestController
@RequiredArgsConstructor
public class MyCourseTablesController {

    private final MyCourseTablesService myCourseTablesService;


    @ApiOperation("添加选课")
    @PostMapping("/choosecourse/{courseId}")
    public XcChooseCourseDto addChooseCourse(@PathVariable("courseId") Long courseId) {
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        if (user == null){
            GlobalException.cast("请先登录");
        }
        String userId = user.getId();
        //添加选课
        XcChooseCourseDto xcChooseCourseDto = myCourseTablesService.addChooseCourse(userId, courseId);
        return xcChooseCourseDto;
    }

    @ApiOperation("查询学习资格")
    @PostMapping("/choosecourse/learnstatus/{courseId}")
    public XcCourseTablesDto getLearnstatus(@PathVariable("courseId") Long courseId) {
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        if (user == null){
            GlobalException.cast("请先登录");
        }
        String userId = user.getId();
        XcCourseTablesDto xcCourseTablesDto = myCourseTablesService.getLearningStatus(userId, courseId);
        return xcCourseTablesDto;

    }

    @ApiOperation("我的课程表")
    @GetMapping("/mycoursetable")
    public PageResult<XcCourseTables> mycoursetable(MyCourseTableParams params) {
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        if (user == null){
            GlobalException.cast("请先登录");
        }
        String userId = user.getId();
        params.setUserId(userId);
        return myCourseTablesService.mycoursetables(params);
    }

}
