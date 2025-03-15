package com.yaojiuye.content.model.dto;

import com.yaojiuye.content.model.po.CourseCategory;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * []
 *
 * @author : [itnan]
 * @version : [v1.0]
 * @createTime : [2025/3/8 17:11]
 */
@Data
public class CourseCategoryTreeDto extends CourseCategory implements Serializable {

    private List<CourseCategoryTreeDto> childrenTreeNodes;
}
