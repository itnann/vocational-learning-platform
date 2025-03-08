package com.yaojiuye.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yaojiuye.base.model.PageParams;
import com.yaojiuye.base.model.PageResult;
import com.yaojiuye.content.model.dto.QueryCourseParamsDto;
import com.yaojiuye.content.model.po.CourseBase;
import com.yaojiuye.content.mapper.CourseBaseMapper;
import com.yaojiuye.content.service.ICourseBaseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

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
}
