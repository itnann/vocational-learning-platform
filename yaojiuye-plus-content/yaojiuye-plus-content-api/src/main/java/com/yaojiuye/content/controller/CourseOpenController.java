package com.yaojiuye.content.controller;

import com.yaojiuye.content.model.dto.CoursePreviewDto;
import com.yaojiuye.content.service.CoursePublishService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author itnan
 * @ClassName CourseOpenController
 * @Description 用户不用登录的都可以查看的接口
 * @Date 2025/4/2 10:13
 * @Version V1.0
 */

@Api(value = "课程公开查询接口",tags = "课程公开查询接口")
@RestController
@RequestMapping("/open")
@RequiredArgsConstructor
public class CourseOpenController {

    private final CoursePublishService coursePublishService;

    @GetMapping("/course/whole/{courseId}")
    public CoursePreviewDto getPreviewInfo(@PathVariable("courseId") Long courseId) {
        //获取课程预览信息
        CoursePreviewDto coursePreviewInfo = coursePublishService.getCoursePreviewInfo(courseId);
        return coursePreviewInfo;
    }

}
