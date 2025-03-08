package com.yaojiuye.content.service;

import com.yaojiuye.base.model.PageParams;
import com.yaojiuye.base.model.PageResult;
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
    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto);

}
