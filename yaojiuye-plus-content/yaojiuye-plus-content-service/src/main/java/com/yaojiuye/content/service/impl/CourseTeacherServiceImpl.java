package com.yaojiuye.content.service.impl;

import com.yaojiuye.base.exception.GlobalException;
import com.yaojiuye.content.mapper.CourseBaseMapper;
import com.yaojiuye.content.model.po.CourseTeacher;
import com.yaojiuye.content.mapper.CourseTeacherMapper;
import com.yaojiuye.content.service.ICourseTeacherService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * <p>
 * 课程-教师关系表 服务实现类
 * </p>
 *
 * @author itnan
 * @since 2025-03-06
 */
@Service
@RequiredArgsConstructor
public class CourseTeacherServiceImpl extends ServiceImpl<CourseTeacherMapper, CourseTeacher> implements ICourseTeacherService {

    private final CourseTeacherMapper courseTeacherMapper;

    private final CourseBaseMapper courseBaseMapper;

    @Override
    public void deleteCourseTeacher(Long companyId, Long id) {
        CourseTeacher courseTeacher = lambdaQuery().eq(CourseTeacher::getId, id).one();
        if(!companyId.equals(courseBaseMapper.selectById(courseTeacher.getCourseId()).getCompanyId())){
            GlobalException.cast("只允许向机构自己的课程中删除老师");
        }
        courseTeacherMapper.deleteById(id);
    }

    @Override
    public CourseTeacher saveOrUpdateCourseTeacher(Long companyId, CourseTeacher courseTeacher) {
        if(!companyId.equals(courseBaseMapper.selectById(courseTeacher.getCourseId()).getCompanyId())){
            GlobalException.cast("只允许向机构自己的课程中添加或修改老师");
        }
        if(courseTeacher.getId() == null){
            //新增
            courseTeacher.setCreateDate(LocalDateTime.now());
            courseTeacherMapper.insert(courseTeacher);
        }else{
            //修改
            courseTeacherMapper.updateById(courseTeacher);
        }
        return courseTeacherMapper.selectById(courseTeacher.getId());
    }
}
