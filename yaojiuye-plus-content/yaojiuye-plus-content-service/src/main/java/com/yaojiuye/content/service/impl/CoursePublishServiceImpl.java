package com.yaojiuye.content.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yaojiuye.base.exception.CommonError;
import com.yaojiuye.base.exception.GlobalException;
import com.yaojiuye.content.config.MultipartSupportConfig;
import com.yaojiuye.content.feignclient.MediaServiceClient;
import com.yaojiuye.content.model.dto.CourseBaseInfoDto;
import com.yaojiuye.content.model.dto.CoursePreviewDto;
import com.yaojiuye.content.model.dto.TeachplanDto;
import com.yaojiuye.content.model.po.*;
import com.yaojiuye.content.mapper.CoursePublishMapper;
import com.yaojiuye.content.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yaojiuye.messagesdk.model.po.MqMessage;
import com.yaojiuye.messagesdk.service.MqMessageService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 课程发布 服务实现类
 * </p>
 *
 * @author itnan
 * @since 2025-03-06
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CoursePublishServiceImpl implements CoursePublishService {

    private final ICourseBaseService courseBaseInfoService;

    private final ITeachplanService teachplanService;

    private final ICourseTeacherService courseTeacherService;

    private final ICoursePublishPreService coursePublishPreService;

    private final ICourseMarketService courseMarketService;

    private final ICoursePublishService coursePublishService;

    private final MqMessageService mqMessageService;

    private final MediaServiceClient mediaServiceClient;


    /**
     * @param courseId 课程id
     * @return com.yaojiuye.content.model.dto.CoursePreviewDto
     * @description 获取课程预览信息
     * @author itnan
     */
    @Override
    public CoursePreviewDto getCoursePreviewInfo(Long courseId) {
        //课程基本信息、营销信息
        CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);

        //课程计划信息
        List<TeachplanDto> teachplanTree= teachplanService.findTeachplanTree(courseId);

        List<CourseTeacher> teacherList = courseTeacherService.list(new LambdaQueryWrapper<CourseTeacher>().eq(CourseTeacher::getCourseId, courseId));

        CoursePreviewDto coursePreviewDto = new CoursePreviewDto();
        coursePreviewDto.setCourseBase(courseBaseInfo);
        coursePreviewDto.setTeachplans(teachplanTree);
        coursePreviewDto.setCourseTeachers(teacherList);
        return coursePreviewDto;
    }

    /**
     * @param companyId
     * @param courseId  课程id
     * @return void
     * //TODO 审核后改为 202004审核通过 202001审核失败
     * @description 提交审核
     * @author itnan
     */
    @Transactional
    @Override
    public void commitAudit(Long companyId, Long courseId) {
        //约束校验
        CourseBaseInfoDto courseBase = courseBaseInfoService.getCourseBaseInfo(courseId);
        //课程计划信息
        List<TeachplanDto> teachplanTree= teachplanService.findTeachplanTree(courseId);
        //课程审核状态
        String auditStatus = courseBase.getAuditStatus();
        //当前审核状态为已提交不允许再次提交
        if("202003".equals(auditStatus)){
            GlobalException.cast("当前为等待审核状态，审核完成可以再次提交。");
        }
        //本机构只允许提交本机构的课程
        if(!courseBase.getCompanyId().equals(companyId)){
            GlobalException.cast("不允许提交其它机构的课程。");
        }
        //课程图片是否填写
        if(StringUtils.isEmpty(courseBase.getPic())){
            GlobalException.cast("提交失败，请上传课程图片");
        }
        //集合工具判断集合是否为空
        if(CollectionUtils.isEmpty(teachplanTree)){
            GlobalException.cast("提交失败，请填写课程计划信息");
        }
        //添加课程预发布记录
        CoursePublishPre coursePublishPre = new CoursePublishPre();
        //查询课程基本信息,课程营销信息,课程计划,课程教师信息
        BeanUtils.copyProperties(courseBase,coursePublishPre); //课程基本信息
        CourseMarket courseMarket = courseMarketService.getById(courseId);
        String courseMarketJson = JSON.toJSONString(courseMarket);
        coursePublishPre.setMarket(courseMarketJson);//课程营销信息
        String teachplanTreeJson = JSON.toJSONString(teachplanTree);//课程计划信息
        coursePublishPre.setTeachplan(teachplanTreeJson);
        List<CourseTeacher> teacherList = courseTeacherService.list(new LambdaQueryWrapper<CourseTeacher>().eq(CourseTeacher::getCourseId, courseId));
        String teacherListJson = JSON.toJSONString(teacherList);
        coursePublishPre.setTeachers(teacherListJson);//教师信息
        //状态为已提交
        coursePublishPre.setStatus("202003");
        //教学机构id
        coursePublishPre.setCompanyId(companyId);
        //提交时间
        coursePublishPre.setCreateDate(LocalDateTime.now());
        CoursePublishPre coursePublishPreUpdate = coursePublishPreService.getById(courseId);
        if(coursePublishPreUpdate == null){
            //添加课程预发布记录
            coursePublishPreService.save(coursePublishPre);
        }else{
            coursePublishPreService.updateById(coursePublishPre);
        }
        //更新课程基本表的审核状态
        courseBase.setAuditStatus("202003");
        courseBaseInfoService.updateById(courseBase);
    }

    /**
     * @param companyId 机构id
     * @param courseId  课程id
     * @return void
     * @description 课程发布接口
     * @author itnan
     */
    @Transactional
    @Override
    public void publish(Long companyId, Long courseId) {
        //约束校验
        //查询课程预发布表
        CoursePublishPre coursePublishPre = coursePublishPreService.getById(courseId);
        if(coursePublishPre == null){
            GlobalException.cast("请先提交课程审核，审核通过才可以发布");
        }
        //本机构只允许提交本机构的课程
        if(!coursePublishPre.getCompanyId().equals(companyId)){
            GlobalException.cast("不允许提交其它机构的课程。");
        }
        if(!"202004".equals(coursePublishPre.getStatus())){
            GlobalException.cast("课程没有审核通过，请审核通过后再发布");
        }
        //向课程发布表中写入数据
        CoursePublish coursePublish = new CoursePublish();
        BeanUtils.copyProperties(coursePublishPre,coursePublish);
        coursePublish.setStatus("203002");//更改状态为发布状态
        coursePublishService.saveOrUpdate(coursePublish); //课程之前发布过,又修改又发布,保存或者更新
        //向消息表写入数据
        saveCoursePublishMessage(courseId);
        //更新课程基本表的发布状态:已发布
        CourseBase courseBase = courseBaseInfoService.getById(courseId);
        courseBase.setStatus("203002");
        courseBaseInfoService.updateById(courseBase);

        //删除课程预发布表中的数据
        coursePublishPreService.removeById(courseId);
    }

    /**
     * @param courseId 课程id
     * @return File 静态化文件
     * @description 课程静态化
     * @author itnan
     */
    @Override
    public File generateCourseHtml(Long courseId) {
        File htmlFile = null;
        try {
            //配置freemarker
            Configuration configuration = new Configuration(Configuration.getVersion());
            //加载模板
            //选指定模板路径,classpath下templates下
            //得到classpath路径
            //String classpath = this.getClass().getResource("/").getPath();
            String classpath = this.getClass().getClassLoader().getResource("").getPath();
            configuration.setDirectoryForTemplateLoading(new File(classpath + "/templates/"));
            //设置字符集
            configuration.setDefaultEncoding("utf-8");
            //指定模板文件名称
            Template template = configuration.getTemplate("course_template.ftl");

            CoursePreviewDto coursePreviewInfo = this.getCoursePreviewInfo(courseId);
            HashMap<String, Object> map = new HashMap<>();
            map.put("model", coursePreviewInfo);

            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);

            InputStream inputStream = IOUtils.toInputStream(html, "UTF-8");
            htmlFile = File.createTempFile("coursepublish", ".html");
            FileOutputStream outputStream = new FileOutputStream(htmlFile);
            IOUtils.copy(inputStream, outputStream);
        } catch (Exception e) {
            log.error("页面静态化出现问题,课程id:{}", courseId, e);
            e.printStackTrace();
        }
        //在try里面定义的 File htmlFile 是局部变量，在方法执行完之后会被回收，所以需要在方法外面定义一个htmlFile，
        return htmlFile;
    }

    /**
     * @param courseId
     * @param file     静态化文件
     * @return void
     * @description 上传课程静态化页面
     * @author itnan
     */
    @Override
    public void uploadCourseHtml(Long courseId, File file) {

        MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(file);
        String s = mediaServiceClient.uploadFile(multipartFile, "course/" + courseId + ".html");
        if (StringUtils.isEmpty(s)) {
            log.debug("远程调用熔断走的降级逻辑, 课程id: {}", courseId);
            GlobalException.cast("上传静态文件远程调用熔断走的降级逻辑");
        }
    }

    /**
     * @description 保存消息表记录
     * @param courseId  课程id
     * @return void
     * @author itnan
     */
    private void saveCoursePublishMessage(Long courseId){
        MqMessage mqMessage = mqMessageService.addMessage("course_publish", String.valueOf(courseId), null, null);
        if(mqMessage==null){
            GlobalException.cast(CommonError.UNKOWN_ERROR);
        }
    }

}
