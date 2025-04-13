package com.yaojiuye.ucenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yaojiuye.base.exception.GlobalException;
import com.yaojiuye.base.model.RestResponse;
import com.yaojiuye.ucenter.feignclient.CheckCodeClient;
import com.yaojiuye.ucenter.mapper.XcUserMapper;
import com.yaojiuye.ucenter.mapper.XcUserRoleMapper;
import com.yaojiuye.ucenter.model.dto.RegisterParamsDto;
import com.yaojiuye.ucenter.model.po.XcUser;
import com.yaojiuye.ucenter.model.po.XcUserRole;
import com.yaojiuye.ucenter.service.RegisterService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author itnan
 * @ClassName RegisterServiceImpl
 * @Description 注册账号Impl
 * @Date 2025/4/13 21:04
 * @Version V1.0
 */
@Service
public class RegisterServiceImpl implements RegisterService {

    @Autowired
    private XcUserMapper xcUserMapper;

    @Autowired
    private CheckCodeClient checkCodeClient;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    XcUserRoleMapper xcUserRoleMapper;

    @Override
    public RestResponse register(RegisterParamsDto registerParamsDto) {
        String key = registerParamsDto.getCheckcodekey();
        String code = registerParamsDto.getCheckcode();
        if(StringUtils.isBlank(key) || StringUtils.isBlank(code)){
            GlobalException.cast("请输入验证码");
        }
        Boolean verify = checkCodeClient.verify(key, code);
        if (!verify){
            GlobalException.cast("验证码错误");
        }
        //判断两次密码是否一致
        String confirmpwd = registerParamsDto.getConfirmpwd();
        String password = registerParamsDto.getPassword();
        if(!password.equals(confirmpwd)){
            GlobalException.cast("两次密码不一致");
        }
        String cellphone = registerParamsDto.getCellphone();
        String email = registerParamsDto.getEmail();
        XcUser xcUser_phone = null;
        XcUser xcUser_email = null;
        if(StringUtils.isNotBlank(cellphone)){
            xcUser_phone = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getCellphone, cellphone));
        }
        if(StringUtils.isNotBlank(email)){
            xcUser_email = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getEmail, email));
        }
        if(xcUser_phone != null || xcUser_email != null){
            GlobalException.cast("手机号或邮箱已存在");
        }
        XcUser xcUser = new XcUser();
        String userId = UUID.randomUUID().toString();
        xcUser.setId(userId);
        xcUser.setNickname(registerParamsDto.getNickname());
        xcUser.setName(registerParamsDto.getNickname());
        xcUser.setUsername(registerParamsDto.getUsername());
        xcUser.setPassword(passwordEncoder.encode(registerParamsDto.getPassword()));
        xcUser.setUtype("101001");//学生类型
        xcUser.setStatus("1");//用户状态
        xcUser.setCreateTime(LocalDateTime.now());
        xcUserMapper.insert(xcUser);
        XcUserRole xcUserRole = new XcUserRole();
        xcUserRole.setId(UUID.randomUUID().toString());
        xcUserRole.setUserId(userId);
        xcUserRole.setRoleId("17");//学生角色
        xcUserRoleMapper.insert(xcUserRole);
        // 构建响应结果
        return RestResponse.success("注册成功,2秒后返回登陆页面", "200");

    }
}
