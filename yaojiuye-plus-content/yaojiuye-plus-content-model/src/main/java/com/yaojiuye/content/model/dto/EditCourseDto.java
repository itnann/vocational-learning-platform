package com.yaojiuye.content.model.dto;

import com.yaojiuye.base.exception.ValidationGroups;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;



/**
 * @description 添加课程dto
 * @author itnan
 * @version 1.0
 */
@Data
@ApiModel(value="EditCourseDto", description="修改课程基本信息")
public class EditCourseDto extends AddCourseDto {

    @NotNull(message = "课程id不能为空", groups = {ValidationGroups.Update.class})
    @ApiModelProperty(value = "课程id", required = true)
    private Long id;

}