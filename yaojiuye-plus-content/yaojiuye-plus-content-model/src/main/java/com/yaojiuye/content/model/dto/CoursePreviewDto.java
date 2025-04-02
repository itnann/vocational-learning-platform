package com.yaojiuye.content.model.dto;

import com.yaojiuye.content.model.po.CourseTeacher;
import lombok.Data;

import java.util.List;

/**
 * @author itnan
 * @ClassName CoursePreviewDto
 * @Description 课程预览数据模型
 * @Date 2025/4/1 21:26
 * @Version V1.0
 */
@Data
public class CoursePreviewDto {

    //课程基本信息,课程营销信息
    CourseBaseInfoDto courseBase;


    //课程计划信息
    List<TeachplanDto> teachplans;

    //师资信息
    List<CourseTeacher> courseTeachers;
}
