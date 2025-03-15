package com.yaojiuye.content.controller;

import com.yaojiuye.content.model.dto.CourseCategoryTreeDto;
import com.yaojiuye.content.service.ICourseCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * [课程分类详细情况]
 *
 * @author : [itnan]
 * @version : [v1.0]
 * @createTime : [2025/3/8 17:18]
 */

@RestController
@Slf4j
@RequiredArgsConstructor
public class CourseCategoryController {

    private final ICourseCategoryService courseCategoryService;

    @GetMapping("/course-category/tree-nodes")
    public List<CourseCategoryTreeDto> queryTreeNodes() {
        return courseCategoryService.queryTreeNodes("1");
    }

}
