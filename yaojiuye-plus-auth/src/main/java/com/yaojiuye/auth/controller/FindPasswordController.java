package com.yaojiuye.auth.controller;

import com.yaojiuye.base.model.RestResponse;
import com.yaojiuye.ucenter.model.dto.FindPasswordParamsDto;
import com.yaojiuye.ucenter.service.FindPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author itnan
 * @ClassName FindPasswordController
 * @Description 找回密码
 * @Date 2025/4/12 20:37
 * @Version V1.0
 */
@RestController
public class FindPasswordController {

    @Autowired
    private FindPasswordService findPasswordService;

    @PostMapping("/findpassword")
    public RestResponse findpassword(@RequestBody FindPasswordParamsDto findPasswordParamsDto) {
        return findPasswordService.findPassword(findPasswordParamsDto);
    }
}
