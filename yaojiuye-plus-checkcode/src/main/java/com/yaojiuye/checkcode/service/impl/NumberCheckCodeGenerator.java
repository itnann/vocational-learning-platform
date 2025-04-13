package com.yaojiuye.checkcode.service.impl;

import com.yaojiuye.checkcode.service.CheckCodeService;
import org.springframework.stereotype.Component;

/**
 * @author itnan
 * @ClassName NumberCheckCodeGenerator
 * @Description 数字验证码生成器
 * @Date 2025/4/12 21:48
 * @Version V1.0
 */
@Component("NumberCheckCodeGenerator")
public class NumberCheckCodeGenerator implements CheckCodeService.CheckCodeGenerator{
    /**
     * 验证码生成
     *double型强制转换成int型 直接舍掉小数，只留下整数
     * @param length
     * @return 验证码
     */
    @Override
    public String generate(int length) {
        //随即生成4个数字,每个数字在0-9之间
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append((int)(Math.random()*10));
        }
        return sb.toString();

    }
}
