package com.yaojiuye.content.service;

import com.yaojiuye.content.model.dto.CourseCategoryTreeDto;
import com.yaojiuye.content.model.po.CourseCategory;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 课程分类 服务类
 * </p>
 *
 * @author itnan
 * @since 2025-03-06
 */
public interface ICourseCategoryService extends IService<CourseCategory> {


        /**
         * 课程分类树形结构查询
         *
         * @return
         */
        public List<CourseCategoryTreeDto> queryTreeNodes(String id);


}
