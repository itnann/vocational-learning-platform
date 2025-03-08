package com.yaojiuye.content.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 课程分类
 * </p>
 *
 * @author itnan
 * @since 2025-03-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("course_category")
@ApiModel(value="CourseCategory对象", description="课程分类")
public class CourseCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private String id;

    @ApiModelProperty(value = "分类名称")
    private String name;

    @ApiModelProperty(value = "分类标签默认和名称一样")
    private String label;

    @ApiModelProperty(value = "父结点id（第一级的父节点是0，自关联字段id）")
    private String parentid;

    @ApiModelProperty(value = "是否显示")
    private Integer isShow;

    @ApiModelProperty(value = "排序字段")
    private Integer orderby;

    @ApiModelProperty(value = "是否叶子")
    private Integer isLeaf;


}
