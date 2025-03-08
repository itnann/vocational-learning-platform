package com.yaojiuye.content.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author itnan
 * @since 2025-03-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("teachplan_media")
@ApiModel(value="TeachplanMedia对象", description="")
public class TeachplanMedia implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "媒资文件id")
    private String mediaId;

    @ApiModelProperty(value = "课程计划标识")
    private Long teachplanId;

    @ApiModelProperty(value = "课程标识")
    private Long courseId;

    @ApiModelProperty(value = "媒资文件原始名称")
    private String mediaFilename;

    private LocalDateTime createDate;

    @ApiModelProperty(value = "创建人")
    private String createPeople;

    @ApiModelProperty(value = "修改人")
    private String changePeople;


}
