package com.yaojiuye.content;

import com.yaojiuye.content.mapper.TeachplanMapper;
import com.yaojiuye.content.model.dto.TeachplanDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author itnan
 * @ClassName TeachplanMapperTests
 * @Description 测试课程计划Mapper
 * @Date 2025/3/15 16:45
 * @Version V1.0
 */
@SpringBootTest
@Slf4j
public class TeachplanMapperTests {

    @Autowired
    private TeachplanMapper teachplanMapper;

    @Test
    public void testSelectTreeNodes() {

        List<TeachplanDto> teachplanDtos = teachplanMapper.selectTreeNodes(117L);
        teachplanDtos.forEach(item -> {
            log.info("{}", item);
        });
    }
}
