package com.yaojiuye.content.model.dto;

import com.yaojiuye.content.model.po.Teachplan;
import com.yaojiuye.content.model.po.TeachplanMedia;
import lombok.Data;

import java.util.List;

/**
 * @author itnan
 * @ClassName TeachplanDto
 * @Description 课程计划信息模型类
 * @Date 2025/3/15 11:43
 * @Version V1.0
 */
@Data
public class TeachplanDto extends Teachplan {

    //课程计划关联的媒资信息
    private TeachplanMedia teachplanMedia;

    //子结点
    private List<TeachplanDto> teachPlanTreeNodes;
}
