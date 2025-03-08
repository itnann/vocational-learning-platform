package com.yaojiuye.base.model;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * []
 *
 * @author : [itnan]
 * @version : [v1.0]
 * @description : [分页查询结果]
 * @createTime : [2025/3/5 17:25]
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult<T> implements Serializable {

    // 数据列表
    private List<T> items;

    //总记录数
    private long counts;

    //当前页码
    private long page;

    //每页记录数
    private long pageSize;


    //通用PO转为Vo
    public static <PO, VO> PageResult<VO> po2Vo(Page<PO> page, Function<PO, VO> convertor) {
        PageResult<VO> pageResult = new PageResult();
        pageResult.setPage(page.getCurrent());
        pageResult.setPageSize(page.getSize());
        pageResult.setCounts(page.getTotal());
        //数据列表
        if(CollectionUtils.isEmpty(page.getRecords())){
            pageResult.setItems(Collections.emptyList());
            return pageResult;
        }
        //转换成VO
        pageResult.setItems(page.getRecords().stream().map(convertor).collect(Collectors.toList()));
        return pageResult;
    }

    public static <PO> PageResult<PO> po2Po(Page<PO> page) {
        PageResult<PO> pageResult = new PageResult();
        pageResult.setPage(page.getCurrent());
        pageResult.setPageSize(page.getSize());
        pageResult.setCounts(page.getTotal());
        //数据列表
        if(CollectionUtils.isEmpty(page.getRecords())){
            pageResult.setItems(Collections.emptyList());
            return pageResult;
        }
        //不变不用转换
        pageResult.setItems(page.getRecords());
        return pageResult;
    }

}
