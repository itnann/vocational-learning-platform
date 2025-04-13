package com.yaojiuye.ucenter.model.dto;

import lombok.Data;

/**
 * @author itnan
 * @ClassName RegisterParamDto
 * @Description 注册账号参数
 * @Date 2025/4/13 20:55
 * @Version V1.0
 */
@Data
public class RegisterParamsDto {

    private String cellphone;

    private String username;

    private String email;

    private String nickname;

    private String password;

    private String confirmpwd;

    private String checkcodekey;;

    private String checkcode;
}
