package com.yaojiuye.content.controller;

import com.yaojiuye.content.service.CoursePublishService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author itnan
 * @ClassName CoursePublishController
 * @Description 课程预览，发布
 * @Date 2025/4/1 20:51
 * @Version V1.0
 */
@Controller
@RequiredArgsConstructor
public class CoursePublishController {

    private final CoursePublishService coursePublishService;

    @GetMapping("/coursepreview/{courseId}")
    public ModelAndView preview(@PathVariable("courseId") Long courseId){
        ModelAndView modelAndView = new ModelAndView();
        //modelAndView.addObject("model",null);
        modelAndView.addObject("model",coursePublishService.getCoursePreviewInfo(courseId));
        modelAndView.setViewName("course_template");
        return modelAndView;
    }
}
