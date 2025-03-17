package com.yaojiuye.content.service;

import com.yaojiuye.content.model.po.CourseTeacher;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 课程-教师关系表 服务类
 * </p>
 *
 * @author itnan
 * @since 2025-03-06
 */
public interface ICourseTeacherService extends IService<CourseTeacher> {



    /**
     * 删除课程-教师关系
     * @param companyId
     * @param id
     */
    void deleteCourseTeacher(Long companyId, Long id);

    /**
     * 保存或更新课程-教师关系
     * @param companyId
     * @param courseTeacher
     * @return
     */
    CourseTeacher saveOrUpdateCourseTeacher(Long companyId, CourseTeacher courseTeacher);
}
