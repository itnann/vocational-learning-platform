package com.yaojiuye.checkcode.service.impl;

import com.yaojiuye.checkcode.model.CheckCodeParamsDto;
import com.yaojiuye.checkcode.model.CheckCodeResultDto;
import com.yaojiuye.checkcode.service.CheckCodeService;
import com.yaojiuye.checkcode.service.SendCheckCodeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author itnan
 * @ClassName SendCheckCodeServiceImpl
 * @Description 发送验证码手机
 * @Date 2025/4/12 22:10
 * @Version V1.0
 */
@Service
public class SendCheckCodeServiceImpl implements SendCheckCodeService {

    @Resource(name = "NumCheckCodeService")
    private CheckCodeService numCheckCodeService;

    @Override
    public CheckCodeResultDto sendCheckCodeByPhone(CheckCodeParamsDto checkCodeParamsDto) {

        CheckCodeResultDto checkCodeResultDto = numCheckCodeService.generate(checkCodeParamsDto);
        String param1 = checkCodeParamsDto.getParam1();
        //手机号正则表达式
        String regexPhone = "^1[3-9]\\d{9}$";
        //邮箱正则表达式
        String regexEmail = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";

        if (!(param1.matches(regexPhone) || param1.matches(regexEmail))) {
            throw new RuntimeException("手机号或邮箱格式错误");
        }
       //验证码发送成功
        System.out.println("验证码发送成功: " + checkCodeResultDto.getAliasing());
        checkCodeResultDto.setAliasing(null);
        return checkCodeResultDto;
    }
}
