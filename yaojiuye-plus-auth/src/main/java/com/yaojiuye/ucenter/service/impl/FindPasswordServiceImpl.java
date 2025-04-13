package com.yaojiuye.ucenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yaojiuye.base.exception.GlobalException;
import com.yaojiuye.base.model.RestResponse;
import com.yaojiuye.ucenter.feignclient.CheckCodeClient;
import com.yaojiuye.ucenter.mapper.XcMenuMapper;
import com.yaojiuye.ucenter.mapper.XcUserMapper;
import com.yaojiuye.ucenter.model.dto.FindPasswordParamsDto;
import com.yaojiuye.ucenter.model.po.XcUser;
import com.yaojiuye.ucenter.service.FindPasswordService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author itnan
 * @ClassName FindPasswordServiceImpl
 * @Description 找回密码实现类
 * @Date 2025/4/12 20:40
 * @Version V1.0
 */
@Service
public class FindPasswordServiceImpl implements FindPasswordService {

    @Autowired
    private XcUserMapper xcUserMapper;

    @Autowired
    private CheckCodeClient checkCodeClient;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public RestResponse findPassword(FindPasswordParamsDto findPasswordParamsDto) {
        //校验验证码
        String key = findPasswordParamsDto.getCheckcodekey();
        String code = findPasswordParamsDto.getCheckcode();
        if(StringUtils.isBlank(key) || StringUtils.isBlank(code)){
            GlobalException.cast("请输入验证码");
        }
        Boolean verify = checkCodeClient.verify(key, code);
        if (!verify){
            GlobalException.cast("验证码错误");
        }
        //判断两次密码是否一致
        String confirmpwd = findPasswordParamsDto.getConfirmpwd();
        String password = findPasswordParamsDto.getPassword();
        if(!password.equals(confirmpwd)){
            GlobalException.cast("两次密码不一致");
        }
        //校验账号是否存在
        String cellphone = findPasswordParamsDto.getCellphone();
        String email = findPasswordParamsDto.getEmail();
        XcUser xcUser = null;
        if(StringUtils.isNotBlank(cellphone)){
            xcUser = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getCellphone, cellphone));
        }else{
            xcUser = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getEmail, email));
        }
        if(xcUser == null){
            GlobalException.cast("账号不存在");
        }
        xcUser.setPassword(passwordEncoder.encode(password));
        xcUser.setUpdateTime(LocalDateTime.now());
        int i = xcUserMapper.updateById(xcUser);
        if(i <= 0){
            GlobalException.cast("修改失败");
        }
        // 构建响应结果
        return RestResponse.success("找回成功", "200");

    }
}
