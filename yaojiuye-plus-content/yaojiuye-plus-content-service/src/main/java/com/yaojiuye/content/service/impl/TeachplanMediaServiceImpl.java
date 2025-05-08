package com.yaojiuye.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yaojiuye.base.exception.GlobalException;
import com.yaojiuye.content.mapper.TeachplanMapper;
import com.yaojiuye.content.model.dto.BindTeachplanMediaDto;
import com.yaojiuye.content.model.po.Teachplan;
import com.yaojiuye.content.model.po.TeachplanMedia;
import com.yaojiuye.content.mapper.TeachplanMediaMapper;
import com.yaojiuye.content.service.ITeachplanMediaService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jdk.nashorn.internal.ir.CallNode;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author itnan
 * @since 2025-03-06
 */
@Service
@RequiredArgsConstructor
public class TeachplanMediaServiceImpl extends ServiceImpl<TeachplanMediaMapper, TeachplanMedia> implements ITeachplanMediaService {

    private final TeachplanMediaMapper teachplanMediaMapper;

    private final TeachplanMapper teachplanMapper;

    /**
     * @param bindTeachplanMediaDto
     * @return com.xuecheng.content.model.po.TeachplanMedia
     * @description 教学计划绑定媒资
     * @author itnan
     */
    @Override
    public TeachplanMedia associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto) {
        //教学计划id
        Long teachplanId = bindTeachplanMediaDto.getTeachplanId();
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        if(teachplan==null){
            GlobalException.cast("教学计划不存在");
        }
        Integer grade = teachplan.getGrade();
        if(grade!=2){
            GlobalException.cast("只允许第二级教学计划绑定媒资文件");
        }
        //课程id
        Long courseId = teachplan.getCourseId();

        //先删除原来该教学计划绑定的媒资
        teachplanMediaMapper.delete(new LambdaQueryWrapper<TeachplanMedia>().eq(TeachplanMedia::getTeachplanId,teachplanId));

        //再添加教学计划与媒资的绑定关系
        TeachplanMedia teachplanMedia = new TeachplanMedia();
        teachplanMedia.setCourseId(courseId);
        teachplanMedia.setTeachplanId(teachplanId);
        teachplanMedia.setMediaFilename(bindTeachplanMediaDto.getFileName());
        teachplanMedia.setMediaId(bindTeachplanMediaDto.getMediaId());
        teachplanMedia.setCreateDate(LocalDateTime.now());
        teachplanMediaMapper.insert(teachplanMedia);
        return teachplanMedia;
    }

    /**
     * 教学计划与媒资文件的绑定关系删除
     *
     * @param teachPlanId
     * @param mediaId
     */
    @Override
    public void unassociationMedia(Long teachPlanId, String mediaId) {
        LambdaQueryWrapper<TeachplanMedia> wrapper = new LambdaQueryWrapper<TeachplanMedia>()
                .eq(StringUtils.isNotBlank(mediaId), TeachplanMedia::getMediaId, mediaId)
                .eq(StringUtils.isNotBlank(String.valueOf(teachPlanId)), TeachplanMedia::getTeachplanId, teachPlanId);
        int delete = teachplanMediaMapper.delete(wrapper);
        if (delete <= 0) {
            GlobalException.cast("删除失败");
        }
    }


}
