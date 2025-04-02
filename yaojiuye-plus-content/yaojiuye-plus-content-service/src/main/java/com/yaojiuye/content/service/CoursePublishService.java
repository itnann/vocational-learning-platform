package com.yaojiuye.content.service;

import com.yaojiuye.content.model.dto.CoursePreviewDto;

/**
 * @description 课程预览、发布接口
 * @author itnan
 * @version 1.0
 */
public interface CoursePublishService {

    /**
     * @description 获取课程预览信息
     * @param courseId 课程id
     * @return com.yaojiuye.content.model.dto.CoursePreviewDto
     * @author itnan
     */
    public CoursePreviewDto getCoursePreviewInfo(Long courseId);
}
