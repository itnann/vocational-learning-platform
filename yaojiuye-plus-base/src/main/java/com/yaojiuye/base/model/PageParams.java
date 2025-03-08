package com.yaojiuye.base.model;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * []
 *
 * @author : [itnan]
 * @version : [v1.0]
 * @description : [分页参数]
 * @createTime : [2025/3/5 17:05]
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageParams {

    @ApiModelProperty("当前页码")
    private Long pageNo = 1L;

    @ApiModelProperty("每页记录数默认值")
    private Long pageSize =10L;

    /*@ApiModelProperty("排序字段")
    private String sortBy;*/

    /*@ApiModelProperty("是否升序")
    private Boolean isAsc;*/

    public <T> Page<T> toMpPage(){
        Page<T> page = new Page<>(pageNo, pageSize);
        return page;
    }



}
