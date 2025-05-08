package com.yaojiuye.learning.service;


import com.yaojiuye.base.model.PageResult;
import com.yaojiuye.learning.model.dto.MyCourseTableParams;
import com.yaojiuye.learning.model.dto.XcChooseCourseDto;
import com.yaojiuye.learning.model.dto.XcCourseTablesDto;
import com.yaojiuye.learning.model.po.XcCourseTables;

/**
 * @description 选课记录service接口 我的课表
 * @author itnan
 * @version 1.0
 */
public interface MyCourseTablesService {

    /**
     * @description 添加选课
     * @param userId 用户id
     * @param courseId 课程id
     * @return com.yaojiuye.learning.model.dto.XcChooseCourseDto
     * @author itnan
     */
    public XcChooseCourseDto addChooseCourse(String userId, Long courseId);

    /**
     * @description 判断学习资格
     * @param userId
     * @param courseId
     * @return XcCourseTablesDto 学习资格状态 [{"code":"702001","desc":"正常学习"},{"code":"702002","desc":"没有选课或选课后没有支付"},{"code":"702003","desc":"已过期需要申请续期或重新支付"}]
     * @author itnan
     */
    public XcCourseTablesDto getLearningStatus(String userId, Long courseId);

    /**
     * 收费课程接到支付成功回调后, 更新选课记录并插入我的课程表
     * @param chooseCourseId
     * @return
     */
    public boolean saveChooseCourseSuccess(String chooseCourseId);

    /**
     * @description 我的课程表
     * @param params
     * @return com.xuecheng.base.model.PageResult<com.xuecheng.learning.model.po.XcCourseTables>
     * @author itnan
     * @date 2022/10/27 9:24
     */
    public PageResult<XcCourseTables> mycoursetables(MyCourseTableParams params);
}
