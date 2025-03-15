package com.yaojiuye.content;

import com.yaojiuye.content.mapper.CourseCategoryMapper;
import com.yaojiuye.content.mapper.CourseMarketMapper;
import com.yaojiuye.content.model.dto.CourseCategoryTreeDto;
import com.yaojiuye.content.model.po.CourseMarket;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;


/**
 * []
 *
 * @author : [itnan]
 * @version : [v1.0]
 * @createTime : [2025/3/7 10:18]
 */

@SpringBootTest
@Slf4j
public class CourseMarketMapperTests {

    @Autowired
    CourseMarketMapper courseMarketMapper;

    @Test
    public void testCourseMarketMapper(){
        CourseMarket courseMarket1 = courseMarketMapper.selectById(129);
        log.info("{}", courseMarket1);
        CourseMarket courseMarket = new CourseMarket();
        courseMarket.setId(130L);
        courseMarketMapper.insert(courseMarket);

    }
}
