package com.yaojiuye.content.mapper;

import com.yaojiuye.content.model.dto.CourseCategoryTreeDto;
import com.yaojiuye.content.model.po.CourseCategory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 课程分类 Mapper 接口
 * </p>
 *
 * @author itnan
 * @since 2025-03-06
 */
public interface CourseCategoryMapper extends BaseMapper<CourseCategory> {

    /**
     * 根据id查询课程分类树形结构
     * @param id
     * @return
     */
    public List<CourseCategoryTreeDto> selectTreeNodes(String id);

}
