package com.yaojiuye.content.service.impl;

import com.yaojiuye.content.model.dto.CourseCategoryTreeDto;
import com.yaojiuye.content.model.po.CourseCategory;
import com.yaojiuye.content.mapper.CourseCategoryMapper;
import com.yaojiuye.content.service.ICourseCategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 课程分类 服务实现类
 * </p>
 *
 * @author itnan
 * @since 2025-03-06
 */
@Service
@RequiredArgsConstructor
public class CourseCategoryServiceImpl extends ServiceImpl<CourseCategoryMapper, CourseCategory> implements ICourseCategoryService {

    private final CourseCategoryMapper courseCategoryMapper;

    @Override
    public List<CourseCategoryTreeDto> queryTreeNodes(String id) {
        //调用mapper递归查询出分类信息
        List<CourseCategoryTreeDto> list = courseCategoryMapper.selectTreeNodes(id);

        //封装成List<CourseCategoryTreeDto>
        List<CourseCategoryTreeDto> result = list.stream().filter(item -> item.getParentid().equals(id))
                .sorted(Comparator.comparing(CourseCategoryTreeDto::getOrderby))
                .collect(Collectors.toList());
        //map中的key为父节点id(一级目录)
        Map<String, List<CourseCategoryTreeDto>> map = list.stream().sorted(Comparator.comparing(CourseCategoryTreeDto::getOrderby))
                .collect(Collectors.groupingBy(CourseCategoryTreeDto::getParentid));
        result.forEach(item -> {
            //item中的id就是map中的key
            List<CourseCategoryTreeDto> value = map.get(item.getId());
            if(CollectionUtils.isEmpty(value)){
                item.setChildrenTreeNodes(Collections.EMPTY_LIST);
            }
            item.setChildrenTreeNodes(value);
        });
        return result;
    }
}
