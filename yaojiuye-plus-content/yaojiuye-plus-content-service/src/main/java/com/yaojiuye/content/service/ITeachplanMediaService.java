package com.yaojiuye.content.service;

import com.yaojiuye.content.model.dto.BindTeachplanMediaDto;
import com.yaojiuye.content.model.po.TeachplanMedia;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author itnan
 * @since 2025-03-06
 */
public interface ITeachplanMediaService extends IService<TeachplanMedia> {

    /**
     * @description 教学计划绑定媒资
     * @param bindTeachplanMediaDto
     * @return com.xuecheng.content.model.po.TeachplanMedia
     * @author itnan
     */
    public TeachplanMedia associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto);


    /**
     * 教学计划与媒资文件的绑定关系删除
     * @param teachPlanId
     * @param mediaId
     */
    void unassociationMedia(Long teachPlanId, String mediaId);
}
