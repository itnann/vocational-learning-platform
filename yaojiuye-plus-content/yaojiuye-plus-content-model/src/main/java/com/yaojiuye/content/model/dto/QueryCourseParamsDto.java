package com.yaojiuye.content.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * []
 *
 * @author : [itnan]
 * @version : [v1.0]
 * @description : [课程查询体条件参数]
 * @createTime : [2025/3/5 17:22]
 */
@Data
public class QueryCourseParamsDto {

    @ApiModelProperty("审核状态")
    private String auditStatus;

    @ApiModelProperty("课程名称")
    private String courseName;

    @ApiModelProperty("课程发布状态")
    private String publishStatus;
}
