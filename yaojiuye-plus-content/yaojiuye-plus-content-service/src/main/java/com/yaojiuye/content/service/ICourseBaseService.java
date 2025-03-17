package com.yaojiuye.content.service;

import com.yaojiuye.base.model.PageParams;
import com.yaojiuye.base.model.PageResult;
import com.yaojiuye.content.model.dto.AddCourseDto;
import com.yaojiuye.content.model.dto.CourseBaseInfoDto;
import com.yaojiuye.content.model.dto.EditCourseDto;
import com.yaojiuye.content.model.dto.QueryCourseParamsDto;
import com.yaojiuye.content.model.po.CourseBase;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 课程基本信息 服务类
 * </p>
 *
 * @author itnan
 * @since 2025-03-06
 */
public interface ICourseBaseService extends IService<CourseBase> {

    /**
     * 课程条件查询
     * @param pageParams
     * @param queryCourseParamsDto
     * @return
     */
    PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto);

    /**
     * @description 添加课程基本信息
     * @param companyId  教学机构id
     * @param addCourseDto  课程基本信息
     * @return
     */
    CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto addCourseDto);

    /**
     * @description 根据课程id查询课程信息
     * @param courseId
     * @return
     */
    CourseBaseInfoDto getCourseBaseInfo(Long courseId);


    /**
     * @description 修改课程信息
     * @param companyId  机构id
     * @param editCourseDto  课程信息
     * @return com.yaojiuye.content.model.dto.CourseBaseInfoDto
     * @author itnan
     */
    CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto editCourseDto);

    /**
     * @description 删除课程信息 其中关联的课程营销信息,课程计划,课程计划关联的媒资信息,课程教师都得删除
     * @param courseId
     */
    void deleteCourseBase(Long courseId);

}
