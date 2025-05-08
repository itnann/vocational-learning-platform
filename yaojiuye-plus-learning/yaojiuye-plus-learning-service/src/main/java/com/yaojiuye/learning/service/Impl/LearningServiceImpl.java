package com.yaojiuye.learning.service.Impl;

import com.alibaba.fastjson.JSON;
import com.sun.org.apache.xpath.internal.objects.XNull;
import com.yaojiuye.base.model.RestResponse;
import com.yaojiuye.content.model.dto.TeachplanDto;
import com.yaojiuye.content.model.po.CoursePublish;
import com.yaojiuye.learning.feignclient.ContentServiceClient;
import com.yaojiuye.learning.feignclient.MediaServiceClient;
import com.yaojiuye.learning.model.dto.XcCourseTablesDto;
import com.yaojiuye.learning.service.LearningService;
import com.yaojiuye.learning.service.MyCourseTablesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author itnan
 * @ClassName LearningServiceImpl
 * @Description 学习过程管理实现类
 * @Date 2025/4/28 23:09
 * @Version V1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class LearningServiceImpl implements LearningService {

    private final MyCourseTablesService myCourseTablesService;

    private final ContentServiceClient contentServiceClient;

    private final MediaServiceClient mediaServiceClient;


    /**
     * @param userId
     * @param courseId    课程id
     * @param teachplanId 课程计划id
     * @param mediaId     视频文件id
     * @return com.yaojiuye.base.model.RestResponse<java.lang.String>
     * @description 获取教学视频
     * @author itnan
     */
    @Override
    public RestResponse<String> getVideo(String userId, Long courseId, Long teachplanId, String mediaId) {
        //查询课程信息
        CoursePublish coursepublish = contentServiceClient.getCoursepublish(courseId);
        if (coursepublish == null) {
            RestResponse.validfail("课程不存在");
        }
        //是否可以试学
        String teachplan = coursepublish.getTeachplan();
        List<TeachplanDto> teachplanDtos = JSON.parseArray(teachplan, TeachplanDto.class);
        for (TeachplanDto teachplanDto : teachplanDtos) {
            List<TeachplanDto> teachPlanTreeNodes = teachplanDto.getTeachPlanTreeNodes();
            if (teachPlanTreeNodes != null && teachPlanTreeNodes.size() > 0) {
                for (TeachplanDto teachplanDto1 : teachPlanTreeNodes) {
                    if (teachplanDto1.getId().equals(teachplanId)) {
                        if("1".equals(teachplanDto1.getIsPreview())){
                            return mediaServiceClient.getPlayUrlByMediaId(mediaId);
                        }
                    }
                }
            }
        }

        if(StringUtils.isNotBlank(userId)){
            XcCourseTablesDto learningStatus = myCourseTablesService.getLearningStatus(userId, courseId);
            //学习资格，[{"code":"702001","desc":"正常学习"},{"code":"702002","desc":"没有选课或选课后没有支付"},{"code":"702003","desc":"已过期需要申请续期或重新支付"}]
            String learnStatus = learningStatus.getLearnStatus();
            if (learnStatus.equals("702001")) {
                return mediaServiceClient.getPlayUrlByMediaId(mediaId);
            } else if (learnStatus.equals("702002")) {
                return RestResponse.validfail("无法观看，由于没有选课或选课后没有支付");
            } else if (learnStatus.equals("702003")) {
                return RestResponse.validfail("您的选课已过期需要申请续期或重新支付");
            }
        }
        //未登录或未选课判断是否收费
        String charge = coursepublish.getCharge();
        if (charge.equals("201000")) {//免费可以正常学习
            return mediaServiceClient.getPlayUrlByMediaId(mediaId);
        }
        return RestResponse.validfail("请购买课程后继续学习");
    }
}
