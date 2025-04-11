package com.yaojiuye.ucenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yaojiuye.ucenter.feignclient.CheckCodeClient;
import com.yaojiuye.ucenter.mapper.XcUserMapper;
import com.yaojiuye.ucenter.model.dto.AuthParamsDto;
import com.yaojiuye.ucenter.model.dto.XcUserExt;
import com.yaojiuye.ucenter.model.po.XcUser;
import com.yaojiuye.ucenter.service.AuthService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author itnan
 * @ClassName PasswordServiceImpl
 * @Description 账号密码登录实现
 * @Date 2025/4/11 11:34
 * @Version V1.0
 */
@Service("password_authService")
public class PasswordServiceImpl implements AuthService {

    @Autowired
    private XcUserMapper xcUserMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CheckCodeClient checkCodeClient;

    /**
     * @param authParamsDto 认证参数
     * @return com.yaojiuye.ucenter.model.po.XcUser 用户信息
     * @description 认证方法
     * @author itnan
     */
    @Override
    public XcUserExt execute(AuthParamsDto authParamsDto) {
        // s 就是userName
        String userName = authParamsDto.getUsername();
        //校验验证码
        if(StringUtils.isBlank(authParamsDto.getCheckcodekey()) || StringUtils.isBlank(authParamsDto.getCheckcode())){
            throw new RuntimeException("请输入验证码");
        }
        Boolean verify = checkCodeClient.verify(authParamsDto.getCheckcodekey(), authParamsDto.getCheckcode());
        if (!verify){
            throw new RuntimeException("验证码错误");
        }
        //校验账号是否存在
        //根据userName查询用户信息
        XcUser user = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getUsername, userName));
        if (user == null){
            throw new RuntimeException("账号不存在");
        }
        //校验密码
        boolean matches = passwordEncoder.matches(authParamsDto.getPassword(), user.getPassword());
        if (!matches){
            throw new RuntimeException("账号或密码错误");
        }
        XcUserExt xcUserExt = new XcUserExt();
        BeanUtils.copyProperties(user,xcUserExt);
        return xcUserExt;
    }
}
