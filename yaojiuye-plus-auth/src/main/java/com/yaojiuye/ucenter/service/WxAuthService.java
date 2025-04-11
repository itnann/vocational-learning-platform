package com.yaojiuye.ucenter.service;

import com.yaojiuye.ucenter.model.po.XcUser;

/**
 * 带着授权码,申请令牌,携带令牌查询用户信息,将用户信息写入本项目数据库
 */
public interface WxAuthService {

    public XcUser wxAuth(String code);

}
