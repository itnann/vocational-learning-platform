package com.yaojiuye.auth.controller;

import com.yaojiuye.ucenter.model.po.XcUser;
import com.yaojiuye.ucenter.service.WxAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

/**
 * @author itnan
 * @ClassName WxLoginController
 * @Description 微信拿到授权码重定向的接口
 * @Date 2025/4/11 21:15
 * @Version V1.0
 */
@Slf4j
@Controller
public class WxLoginController {

    @Autowired
    WxAuthService wxAuthService;

    @RequestMapping("/wxLogin")
    public String wxLogin(String code, String state) throws IOException {
        log.debug("微信扫码回调,code:{},state:{}",code,state);
        //请求微信申请令牌，拿到令牌查询用户信息，将用户信息写入本项目数据库
        XcUser xcUser = wxAuthService.wxAuth(code);
        if(xcUser==null){
            return "redirect:http://www.yaojiuye.cn/error.html";
        }
        String username = xcUser.getUsername();
        return "redirect:http://www.yaojiuye.cn/sign.html?username="+username+"&authType=wx";
    }
}
