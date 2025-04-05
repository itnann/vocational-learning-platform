package com.yaojiuye.content.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yaojiuye.content.mapper.CoursePublishMapper;
import com.yaojiuye.content.model.po.CoursePublish;
import com.yaojiuye.content.service.ICoursePublishService;
import org.springframework.stereotype.Service;

/**
 * @author itnan
 * @ClassName ICoursePublishServiceImpl
 * @Description 课程发布接口实现类
 * @Date 2025/4/3 20:57
 * @Version V1.0
 */
@Service
public class ICoursePublishServiceImpl extends ServiceImpl<CoursePublishMapper, CoursePublish> implements ICoursePublishService {
}
