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
 * 课程-教师关系表
 * </p>
 *
 * @author itnan
 * @since 2025-03-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("course_teacher")
@ApiModel(value="CourseTeacher对象", description="课程-教师关系表")
public class CourseTeacher implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "课程标识")
    private Long courseId;

    @ApiModelProperty(value = "教师标识")
    private String teacherName;

    @ApiModelProperty(value = "教师职位")
    private String position;

    @ApiModelProperty(value = "教师简介")
    private String introduction;

    @ApiModelProperty(value = "照片")
    private String photograph;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createDate;


}
