package com.yaojiuye.content.service.jobhandler;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.yaojiuye.base.exception.GlobalException;
import com.yaojiuye.content.feignclient.CourseIndex;
import com.yaojiuye.content.feignclient.SearchServiceClient;
import com.yaojiuye.content.model.po.CoursePublish;
import com.yaojiuye.content.service.CoursePublishService;
import com.yaojiuye.content.service.ICoursePublishService;
import com.yaojiuye.messagesdk.model.po.MqMessage;
import com.yaojiuye.messagesdk.service.MessageProcessAbstract;
import com.yaojiuye.messagesdk.service.MqMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author itnan
 * @ClassName CoursePublishTask
 * @Description 课程发布处理类, 同步到redis缓存,elasticsearch搜索索引,minio课程信息
 * @Date 2025/4/4 11:31
 * @Version V1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CoursePublishTask extends MessageProcessAbstract {

    private final CoursePublishService coursePublishService;

    private final SearchServiceClient searchServiceClient;

    private final ICoursePublishService iCoursePublishService;

    //任务调度入口
    @XxlJob("CoursePublishJobHandler")
    public void coursePublishJobHandler() throws Exception {
        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        log.debug("shardIndex="+shardIndex+",shardTotal="+shardTotal);
        //参数:分片序号、分片总数、消息类型、一次最多取到的任务数量、一次任务调度执行的超时时间
        process(shardIndex,shardTotal,"course_publish",30,60);
    }

    /**
     * @param mqMessage 执行任务内容
     * @return boolean true:处理成功，false处理失败
     * @description 任务处理
     * @author itnan
     */
    @Override
    public boolean execute(MqMessage mqMessage) {
        Long courseId = Long.parseLong(mqMessage.getBusinessKey1());
        //向minio中上传课程静态化数据
        generateCourseHtml(mqMessage,courseId);

        //向elasticsearch索引中添加课程信息
        saveCourseIndex(mqMessage,courseId);

        //向redis缓存中添加课程信息 TODO

        return true;
    }

    //生成课程静态化页面并上传至文件系统 第一阶段
    private void generateCourseHtml(MqMessage mqMessage,long courseId){
        //消息id
        Long id = mqMessage.getId();
        //做任务幂等性处理 一定是要和数据库的进行比较
        //查询数据库取出该阶段的执行状态
        MqMessageService mqMessageService = this.getMqMessageService();
        int stageOne = mqMessageService.getStageOne(id);
        if (stageOne == 1){
            log.info("课程静态化已生成,无需处理");
            return;
        }
        //课程静态化 生成html页面
        File file = coursePublishService.generateCourseHtml(courseId);
        if (file == null){
            log.error("课程静态化失败,课程id:{}",courseId);
            GlobalException.cast("课程静态化页面生成失败");
        }
        //将html页面上传到minio
        coursePublishService.uploadCourseHtml(courseId,file);
        //任务完成,状态更新为完成
        mqMessageService.completedStageOne(id);

    }

    //保存课程索引信息 第二阶段
    public void saveCourseIndex(MqMessage mqMessage,long courseId){
        //消息id
        Long id = mqMessage.getId();
        //做任务幂等性处理 一定是要和数据库的进行比较
        //查询数据库取出该阶段的执行状态
        MqMessageService mqMessageService = this.getMqMessageService();
        int stageTwo = mqMessageService.getStageTwo(id);
        if (stageTwo == 1){
            log.info("课程索引信息已写入,无需执行");
            return;
        }
        //查询课程,添加索引信息
        CoursePublish coursePublish = iCoursePublishService.getById(courseId);
        CourseIndex courseIndex = new CourseIndex();
        BeanUtils.copyProperties(coursePublish,courseIndex);
        boolean result = searchServiceClient.add(courseIndex);
        //任务完成第二阶段,状态更新为完成
        if(!result){
            GlobalException.cast("保存课程索引信息失败");
        }
        mqMessageService.completedStageTwo(id);

    }

    public void saveCourseCache(MqMessage mqMessage,long courseId){

    }
}
