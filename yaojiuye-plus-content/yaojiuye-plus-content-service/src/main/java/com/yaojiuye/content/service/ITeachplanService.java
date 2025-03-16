package com.yaojiuye.content.service;

import com.yaojiuye.content.model.dto.SaveTeachplanDto;
import com.yaojiuye.content.model.dto.TeachplanDto;
import com.yaojiuye.content.model.po.Teachplan;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 课程计划 服务类
 * </p>
 *
 * @author itnan
 * @since 2025-03-06
 */
public interface ITeachplanService extends IService<Teachplan> {


    /**
     * @description 查询课程计划树型结构
     * @param courseId  课程id
     * @return List<TeachplanDto>
     * @author itnan
     */
    public List<TeachplanDto> findTeachplanTree(long courseId);

    /**
     * @description 课程计划创建或修改
     * @param saveTeachplanDto
     */
    public void saveTeachplan(SaveTeachplanDto saveTeachplanDto);

    public void deleteTeachplan(Long id);

    /**
     * @description 课程计划上移
     * @param id
     */
    void movedown(Long id);

    /**
     * @description 课程计划下移
     * @param id
     */
    void moveup(Long id);
}
