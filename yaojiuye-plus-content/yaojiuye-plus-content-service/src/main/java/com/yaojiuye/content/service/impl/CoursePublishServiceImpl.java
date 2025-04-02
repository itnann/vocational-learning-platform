package com.yaojiuye.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yaojiuye.content.model.dto.CourseBaseInfoDto;
import com.yaojiuye.content.model.dto.CoursePreviewDto;
import com.yaojiuye.content.model.dto.TeachplanDto;
import com.yaojiuye.content.model.po.CoursePublish;
import com.yaojiuye.content.mapper.CoursePublishMapper;
import com.yaojiuye.content.model.po.CourseTeacher;
import com.yaojiuye.content.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 课程发布 服务实现类
 * </p>
 *
 * @author itnan
 * @since 2025-03-06
 */
@Service
@RequiredArgsConstructor
public class CoursePublishServiceImpl implements CoursePublishService {

    private final ICourseBaseService courseBaseInfoService;

    private final ITeachplanService teachplanService;

    private final ICourseTeacherService courseTeacherService;

    /**
     * @param courseId 课程id
     * @return com.yaojiuye.content.model.dto.CoursePreviewDto
     * @description 获取课程预览信息
     * @author itnan
     */
    @Override
    public CoursePreviewDto getCoursePreviewInfo(Long courseId) {
        //课程基本信息、营销信息
        CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);

        //课程计划信息
        List<TeachplanDto> teachplanTree= teachplanService.findTeachplanTree(courseId);

        List<CourseTeacher> teacherList = courseTeacherService.list(new LambdaQueryWrapper<CourseTeacher>().eq(CourseTeacher::getCourseId, courseId));

        CoursePreviewDto coursePreviewDto = new CoursePreviewDto();
        coursePreviewDto.setCourseBase(courseBaseInfo);
        coursePreviewDto.setTeachplans(teachplanTree);
        coursePreviewDto.setCourseTeachers(teacherList);
        return coursePreviewDto;
    }
}
