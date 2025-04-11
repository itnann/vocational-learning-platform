package com.yaojiuye.ucenter.service;


import com.yaojiuye.ucenter.model.dto.AuthParamsDto;
import com.yaojiuye.ucenter.model.dto.XcUserExt;

/**
 * @description 统一认证的接口  策略模式
 * @author itnan
 * @version 1.0
 */
public interface AuthService {

    /**
     * @description 认证方法
     * @param authParamsDto 认证参数
     * @return com.xuecheng.ucenter.model.po.XcUser 用户信息
     * @author itnan
     */
    XcUserExt execute(AuthParamsDto authParamsDto);
}
