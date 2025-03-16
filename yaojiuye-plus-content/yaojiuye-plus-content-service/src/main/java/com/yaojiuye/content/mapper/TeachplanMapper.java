package com.yaojiuye.content.mapper;

import com.yaojiuye.content.model.dto.TeachplanDto;
import com.yaojiuye.content.model.po.Teachplan;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 课程计划 Mapper 接口
 * </p>
 *
 * @author itnan
 * @since 2025-03-06
 */
public interface TeachplanMapper extends BaseMapper<Teachplan> {

    //课程计划查询
    //TODO 可以用表的自连接实现 需要自外连接  否则新增大章节后查询不到
    List<TeachplanDto> selectTreeNodes(Long courseId);

}
