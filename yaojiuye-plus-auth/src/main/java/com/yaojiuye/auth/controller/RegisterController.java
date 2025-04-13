package com.yaojiuye.auth.controller;

import com.yaojiuye.base.model.RestResponse;
import com.yaojiuye.ucenter.model.dto.RegisterParamsDto;
import com.yaojiuye.ucenter.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author itnan
 * @ClassName RegisterController
 * @Description 注册账号
 * @Date 2025/4/13 20:48
 * @Version V1.0
 */
@RestController
public class RegisterController {

    @Autowired
    private RegisterService registerService;

    @PostMapping("/register")
    public RestResponse register(@RequestBody RegisterParamsDto registerParamsDto) {
        return registerService.register(registerParamsDto);
    }
}
