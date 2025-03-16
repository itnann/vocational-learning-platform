package com.yaojiuye.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yaojiuye.base.exception.GlobalException;
import com.yaojiuye.content.mapper.TeachplanMediaMapper;
import com.yaojiuye.content.model.dto.SaveTeachplanDto;
import com.yaojiuye.content.model.dto.TeachplanDto;
import com.yaojiuye.content.model.po.Teachplan;
import com.yaojiuye.content.mapper.TeachplanMapper;
import com.yaojiuye.content.model.po.TeachplanMedia;
import com.yaojiuye.content.service.ITeachplanService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 课程计划 服务实现类
 * </p>
 *
 * @author itnan
 * @since 2025-03-06
 */
@Service
@RequiredArgsConstructor
public class TeachplanServiceImpl extends ServiceImpl<TeachplanMapper, Teachplan> implements ITeachplanService {

    private final TeachplanMapper teachplanMapper;

    private final TeachplanMediaMapper teachplanMediaMapper;

    @Override
    public List<TeachplanDto> findTeachplanTree(long courseId) {
        //这个Dtos当中需要处理将大章节的teachPlanTreeNodes属性设置为小章节
        List<TeachplanDto> teachplanDtos = teachplanMapper.selectTreeNodes(courseId);
        List<TeachplanDto> father = teachplanDtos.stream().filter(item -> item.getParentid().equals(0L))
                .sorted(Comparator.comparing(TeachplanDto::getOrderby))
                .collect(Collectors.toList());
        Map<Long, List<TeachplanDto>> children = teachplanDtos.stream().sorted(Comparator.comparing(TeachplanDto::getOrderby))
                .collect(Collectors.groupingBy(TeachplanDto::getParentid));
        father.forEach(item -> {
            List<TeachplanDto> value = children.get(item.getId());
            if (CollectionUtils.isEmpty(value)) {
                item.setTeachPlanTreeNodes(Collections.EMPTY_LIST);
            }
            item.setTeachPlanTreeNodes(value);
        });
        return father;
    }

    @Override
    public void saveTeachplan(SaveTeachplanDto saveTeachplanDto) {
        // 判断是修改还是新增 如果id为空就是新增
        Long id = saveTeachplanDto.getId();
        if (id == null) {
            //新增
            Teachplan teachplan = new Teachplan();
            BeanUtils.copyProperties(saveTeachplanDto, teachplan);
            //确定排序的字段
            teachplan.setOrderby(getTeachplanCount(saveTeachplanDto.getCourseId(), saveTeachplanDto.getParentid()) + 1);
            teachplanMapper.insert(teachplan);
        } else {
            //修改
            Teachplan teachplan = teachplanMapper.selectById(id);
            BeanUtils.copyProperties(saveTeachplanDto, teachplan);
            lambdaUpdate().eq(Teachplan::getId, id).update(teachplan);
        }
    }

    /**
     * 根据课程id和父id获取章节的怕排序字段
     *
     * @param courseId
     * @param parentId
     * @return
     */
    private Integer getTeachplanCount(Long courseId, Long parentId) {
        //获取courseId和parentId相同的他们中orderby字段最大的值 ,返回它的字段值
        return lambdaQuery().eq(Teachplan::getCourseId, courseId)
                .eq(Teachplan::getParentid, parentId)
                .orderByDesc(Teachplan::getOrderby)
                .last("limit 1")
                .one().getOrderby();
    }

    @Override
    public void deleteTeachplan(Long id) {
        //1.先判断是大章节还是小章节
        Teachplan teachplan = lambdaQuery().eq(Teachplan::getId, id).one();
        if (teachplan == null) {
            GlobalException.cast("要删除的章节不存在");
        }
        if (teachplan.getGrade() == 1) {
            //说明是大章节
            //如果大章节下有小章节则无法删除
            Integer count = lambdaQuery().eq(Teachplan::getParentid, id).count();
            if (count > 0) {
                GlobalException.cast("该章节下有子章节，无法删除");
            }
            lambdaUpdate().eq(Teachplan::getId, id).remove();
        }
        //2.如果小章节则删除随之绑定的媒资资源也得删除
        lambdaUpdate().eq(Teachplan::getId, id).remove();
        //删除媒资资源
        LambdaQueryWrapper<TeachplanMedia> wrapper = new LambdaQueryWrapper<>();
        teachplanMediaMapper.delete(wrapper.eq(TeachplanMedia::getTeachplanId, id));
    }

    @Override
    public void movedown(Long id) {
        Teachplan teachplan = lambdaQuery().eq(Teachplan::getId, id).one();
        if (teachplan == null) {
            GlobalException.cast("要移动的章节不存在");
        }
        Integer orderby = teachplan.getOrderby();
        //章节则判断是不是最后一个章节
        if (orderby.equals(getTeachplanCount(teachplan.getCourseId(), teachplan.getParentid()))) {
            //相同则说明是最后一个章节，无法下移
            GlobalException.cast("该章节已经是最后一个章节，无法下移");
        }
        //不相同,则找到下一个大章节,下一个大章节的orderby gt 它的orderby
        Teachplan next = lambdaQuery().eq(Teachplan::getCourseId, teachplan.getCourseId())
                .eq(Teachplan::getParentid, teachplan.getParentid())
                .gt(Teachplan::getOrderby, orderby)
                .orderByAsc(Teachplan::getOrderby)
                .last("limit 1").one();
        //交换他们的orderby
        teachplan.setOrderby(next.getOrderby());
        next.setOrderby(orderby);
        lambdaUpdate().eq(Teachplan::getId, id).update(teachplan);
        lambdaUpdate().eq(Teachplan::getId, next.getId()).update(next);
    }

    @Override
    public void moveup(Long id) {
        Teachplan teachplan = lambdaQuery().eq(Teachplan::getId, id).one();
        if (teachplan == null) {
            GlobalException.cast("要移动的章节不存在");
        }
        Integer orderby = teachplan.getOrderby();
        //找到第一个章节是否为它,如果是第一个则无法上移
        Teachplan top1 = lambdaQuery().eq(Teachplan::getCourseId, teachplan.getCourseId())
                .eq(Teachplan::getParentid, teachplan.getParentid())
                .orderByAsc(Teachplan::getOrderby)
                .last("limit 1").one();
        if (orderby.equals(top1.getOrderby())) {
            //相同则说明是第一个章节，无法上移
            GlobalException.cast("该章节已经是第一个章节，无法上移");
        }
        //不相同,则找到上一个章节,上一个大章节的orderby lt 它的orderby
        Teachplan pre = lambdaQuery().eq(Teachplan::getCourseId, teachplan.getCourseId())
                .eq(Teachplan::getParentid, teachplan.getParentid())
                .lt(Teachplan::getOrderby, orderby)
                .orderByDesc(Teachplan::getOrderby)
                .last("limit 1").one();
        //交换他们的orderby
        teachplan.setOrderby(pre.getOrderby());
        pre.setOrderby(orderby);
        lambdaUpdate().eq(Teachplan::getId, id).update(teachplan);
        lambdaUpdate().eq(Teachplan::getId, pre.getId()).update(pre);
    }
}
