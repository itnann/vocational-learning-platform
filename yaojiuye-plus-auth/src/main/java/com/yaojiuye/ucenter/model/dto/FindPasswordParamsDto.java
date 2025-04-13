package com.yaojiuye.ucenter.model.dto;

import lombok.Data;

/**
 * @author itnan
 * @ClassName FindPasswordParamsDto
 * @Description 找回密码前端传过来的数据
 * @Date 2025/4/12 20:34
 * @Version V1.0
 */
@Data
public class FindPasswordParamsDto {

    //手机号
    private String cellphone;
    //邮箱
    private String email;
    //验证码key
    private String checkcodekey;
    //验证码
    private String checkcode;
    //确认密码
    private String confirmpwd;
    //密码
    private String password;
}
