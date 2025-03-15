package com.yaojiuye.content.model.dto;

import com.yaojiuye.base.exception.ValidationGroups;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * @author itnan
 * @version 1.0
 * @description 添加课程dto
 */
@Data
@ApiModel(value = "AddCourseDto", description = "新增课程基本信息")
public class AddCourseDto {

    //@NotEmpty(message = "课程名称不能为空")
    @NotEmpty(message = "课程名称不能为空", groups = {ValidationGroups.Inster.class, ValidationGroups.Update.class})
    @ApiModelProperty(value = "课程名称", required = true)
    private String name;

    @NotEmpty(message = "适用人群不能为空", groups = {ValidationGroups.Inster.class, ValidationGroups.Update.class})
    //@Size(message = "适用人群内容过少", min = 10, groups = {ValidationGroups.Inster.class, ValidationGroups.Update.class})
    @ApiModelProperty(value = "适用人群", required = true)
    private String users;

    @ApiModelProperty(value = "课程标签")
    private String tags;

    @NotEmpty(message = "课程分类不能为空", groups = {ValidationGroups.Inster.class, ValidationGroups.Update.class})
    @ApiModelProperty(value = "大分类", required = true)
    private String mt;

    @NotEmpty(message = "课程分类不能为空", groups = {ValidationGroups.Inster.class, ValidationGroups.Update.class})
    @ApiModelProperty(value = "小分类", required = true)
    private String st;

    @NotEmpty(message = "课程等级不能为空", groups = {ValidationGroups.Inster.class, ValidationGroups.Update.class})
    @ApiModelProperty(value = "课程等级", required = true)
    private String grade;

    @ApiModelProperty(value = "教学模式（普通，录播，直播等）", required = true)
    private String teachmode;

    @ApiModelProperty(value = "课程介绍")
    @Size(message = "课程描述内容过少", min = 10, groups = {ValidationGroups.Inster.class, ValidationGroups.Update.class})
    private String description;

    @ApiModelProperty(value = "课程图片", required = true)
    private String pic;

    @NotEmpty(message = "收费规则不能为空", groups = {ValidationGroups.Inster.class, ValidationGroups.Update.class})
    @ApiModelProperty(value = "收费规则，对应数据字典", required = true)
    private String charge;

    //计算的时候转成BigDecimal避免精度损失
    @ApiModelProperty(value = "价格")
    private Float price;
    @ApiModelProperty(value = "原价")
    private Float originalPrice;


    @ApiModelProperty(value = "qq")
    private String qq;

    @ApiModelProperty(value = "微信")
    private String wechat;
    @ApiModelProperty(value = "电话")
    private String phone;

    @ApiModelProperty(value = "有效期")
    private Integer validDays;
}
