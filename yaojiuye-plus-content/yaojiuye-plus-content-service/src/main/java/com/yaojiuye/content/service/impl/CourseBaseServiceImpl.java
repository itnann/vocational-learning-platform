package com.yaojiuye.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yaojiuye.base.exception.CommonError;
import com.yaojiuye.base.exception.GlobalException;
import com.yaojiuye.base.model.PageParams;
import com.yaojiuye.base.model.PageResult;
import com.yaojiuye.content.mapper.*;
import com.yaojiuye.content.model.dto.AddCourseDto;
import com.yaojiuye.content.model.dto.CourseBaseInfoDto;
import com.yaojiuye.content.model.dto.EditCourseDto;
import com.yaojiuye.content.model.dto.QueryCourseParamsDto;
import com.yaojiuye.content.model.po.*;
import com.yaojiuye.content.service.ICourseBaseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * <p>
 * 课程基本信息 服务实现类
 * </p>
 *
 * @author itnan
 * @since 2025-03-06
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CourseBaseServiceImpl extends ServiceImpl<CourseBaseMapper, CourseBase> implements ICourseBaseService {

    private final CourseBaseMapper courseBaseMapper;

    private final CourseMarketMapper courseMarketMapper;

    private final CourseCategoryMapper courseCategoryMapper;

    private final TeachplanMapper teachplanMapper;

    private final TeachplanMediaMapper teachplanMediaMapper;

    private final CourseTeacherMapper courseTeacherMapper;




    /**
     * 根据条件分页查询课程信息
     *
     * @param pageParams
     * @param queryCourseParamsDto
     * @return
     */
    @Override
    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto) {
        QueryWrapper<CourseBase> wrapper = new QueryWrapper();
        wrapper.lambda()
                .like(StringUtils.isNotEmpty(queryCourseParamsDto.getCourseName()), CourseBase::getName, queryCourseParamsDto.getCourseName())
                .eq(StringUtils.isNotEmpty(queryCourseParamsDto.getAuditStatus()), CourseBase::getAuditStatus, queryCourseParamsDto.getAuditStatus())
                .eq(StringUtils.isNotEmpty(queryCourseParamsDto.getPublishStatus()), CourseBase::getStatus, queryCourseParamsDto.getPublishStatus());
        Page<CourseBase> page = pageParams.toMpPage();
        Page<CourseBase> courseBasePage = courseBaseMapper.selectPage(page, wrapper);
        PageResult<CourseBase> pageResult = PageResult.po2Po(courseBasePage);
        return pageResult;
    }

    @Transactional
    @Override
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto dto) {
        //参数的合法性校验
        //合法性校验
        /*if (StringUtils.isBlank(dto.getName())) {
            //GlobalException.cast("课程名称为空");
            GlobalException.cast("课程名称为空");
        }

        if (StringUtils.isBlank(dto.getMt())) {
            GlobalException.cast("课程分类为空");
        }

        if (StringUtils.isBlank(dto.getSt())) {
            GlobalException.cast("课程分类为空");
        }

        if (StringUtils.isBlank(dto.getGrade())) {
            GlobalException.cast("课程等级为空");
        }

        if (StringUtils.isBlank(dto.getTeachmode())) {
            GlobalException.cast("教育模式为空");
        }

        if (StringUtils.isBlank(dto.getUsers())) {
            GlobalException.cast("适应人群为空");
        }

        if (StringUtils.isBlank(dto.getCharge())) {
            GlobalException.cast("收费规则为空");
        }*/
        //向课程基本信息表course_base插入数据
        CourseBase courseBase = new CourseBase();
        BeanUtils.copyProperties(dto, courseBase);
        courseBase.setCompanyId(companyId)
                .setCreateDate(LocalDateTime.now())
                .setAuditStatus("202002")//审核状态为未审核
                .setStatus("203001");//发布状态为未发布
        int insert = courseBaseMapper.insert(courseBase);
        if (insert < 1) {
            GlobalException.cast("新增课程基本信息失败");
        }
        //向课程营销信息表course_market插入数据
        CourseMarket courseMarket = new CourseMarket();
        BeanUtils.copyProperties(dto, courseMarket);
        courseMarket.setId(courseBase.getId());
        //保存营销信息
        saveCourseMarket(courseMarket);
        //从数据库查出课程的相关的信息,包括两部分
        CourseBaseInfoDto courseBaseInfoDto = getCourseBaseInfo(courseBase.getId());
        return courseBaseInfoDto;
    }

    public CourseBaseInfoDto getCourseBaseInfo(Long courseId) {
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseBase == null) {
            return null;
        }
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);

        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase, courseBaseInfoDto);
        if (courseMarket != null) {
            BeanUtils.copyProperties(courseMarket, courseBaseInfoDto);
        }
        courseBaseInfoDto.setStName(courseCategoryMapper.selectById(courseBase.getSt()).getName());
        courseBaseInfoDto.setMtName(courseCategoryMapper.selectById(courseBase.getMt()).getName());
        return courseBaseInfoDto;
    }

    // 单独写一个用来保存课程营销信息的方法
    private int saveCourseMarket(CourseMarket courseMarket) {
        //参数的合法性校验
        String charge = courseMarket.getCharge();
        if (StringUtils.isBlank(charge)) {
            GlobalException.cast("收费规则为空");
        }
        if(charge.equals("201000")&&courseMarket.getPrice().floatValue() != 0){
            GlobalException.cast("课程为免费课程价格不能为空且必须等于0");
        }
        if (charge.equals("201001")) {
            if (courseMarket.getPrice() == null || courseMarket.getPrice().floatValue() <= 0) {
                GlobalException.cast("课程为收费价格不能为空且必须大于0");
            }
        }
        CourseMarket courseMarketObj = courseMarketMapper.selectById(courseMarket.getId());
        if (courseMarketObj == null) {
            //插入
            return courseMarketMapper.insert(courseMarket);
        } else {
            //更新
            BeanUtils.copyProperties(courseMarket, courseMarketObj);
            return courseMarketMapper.updateById(courseMarketObj);
        }

    }

    @Override
    @Transactional
    public CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto editCourseDto) {
        //先拿到课程id,查询出课程基本信息
        CourseBase courseBase = lambdaQuery().eq(CourseBase::getId, editCourseDto.getId()).one();
        if (courseBase == null) {
            GlobalException.cast("课程基本信息不存在");
        }
        //本机构只能修改本机构的课程
        if (!companyId.equals(courseBase.getCompanyId())) {
            GlobalException.cast("本机构只能修改本机构的课程");
        }
        //封装数据
        BeanUtils.copyProperties(editCourseDto, courseBase);
        //更新修改时间
        courseBase.setChangeDate(LocalDateTime.now());
        //更新课程基本信息
        boolean update_CourseBase = lambdaUpdate().eq(CourseBase::getId, courseBase.getId()).update(courseBase);
        if (!update_CourseBase) {
            GlobalException.cast("更新课程基本信息失败");
        }
        //更新课程营销信息,先删除课程营销信息，再插入课程营销信息
        CourseMarket courseMarket = new CourseMarket();
        LambdaQueryWrapper<CourseMarket> wrapper = new LambdaQueryWrapper<>();
        courseMarketMapper.delete(wrapper.eq(CourseMarket::getId, editCourseDto.getId()));
        BeanUtils.copyProperties(editCourseDto, courseMarket);
        saveCourseMarket(courseMarket);
        //返回课程基本信息
        return getCourseBaseInfo(editCourseDto.getId());
    }

    /**
     * @param courseId
     * @description 删除课程信息 其中关联的课程营销信息,课程计划,课程计划关联的媒资信息,课程教师都得删除
     */
    @Override
    @Transactional
    public void deleteCourseBase(Long courseId) {
        //先判断该课程计划是否存在 并且课程审核状态为未提交
        CourseBase courseBase = courseBaseMapper.selectOne(new LambdaQueryWrapper<CourseBase>().eq(CourseBase::getId, courseId).eq(CourseBase::getAuditStatus, "202002"));
        if (courseBase == null) {
            GlobalException.cast("课程不存在");
        }
        //删除课程基本信息
        boolean delete_CourseBase = lambdaUpdate().eq(CourseBase::getId, courseId).remove();
        if (!delete_CourseBase) {
            GlobalException.cast("删除课程基本信息失败");
        }
        //删除课程营销信息
        boolean delete_CourseMarket = courseMarketMapper.deleteById(courseId) > 0;
        if (!delete_CourseMarket) {
            GlobalException.cast("删除课程营销信息失败");
        }
        //删除课程计划信息
       teachplanMapper.delete(new LambdaQueryWrapper<Teachplan>().eq(Teachplan::getCourseId, courseId));

        //删除课程计划关联的媒资信息
        teachplanMediaMapper.delete(new LambdaQueryWrapper<TeachplanMedia>().eq(TeachplanMedia::getCourseId, courseId));

        //删除课程教师信息
        courseTeacherMapper.delete(new LambdaQueryWrapper<CourseTeacher>().eq(CourseTeacher::getCourseId, courseId));

    }
}
