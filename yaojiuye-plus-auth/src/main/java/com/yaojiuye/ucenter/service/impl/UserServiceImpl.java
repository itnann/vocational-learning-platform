package com.yaojiuye.ucenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yaojiuye.ucenter.mapper.XcMenuMapper;
import com.yaojiuye.ucenter.model.dto.AuthParamsDto;
import com.yaojiuye.ucenter.model.dto.XcUserExt;
import com.yaojiuye.ucenter.model.po.XcMenu;
import com.yaojiuye.ucenter.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author itnan
 * @ClassName UserServiceImpl
 * @Description UserDetailsService的实现类
 * @Date 2025/4/6 23:00
 * @Version V1.0
 */
@Component
@Slf4j
public class UserServiceImpl implements UserDetailsService {

    @Autowired  //Spring的IOC容器
    ApplicationContext applicationContext;

    @Autowired
    XcMenuMapper xcMenuMapper;

    //传入请求认证的参数就是AuthParamsDto 只不过是json串
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        //将json字符串s转为AuthParamsDto对象
        AuthParamsDto authParamsDto = null;
        //将字符串转为json对象
        try {
            authParamsDto = JSONObject.parseObject(s, AuthParamsDto.class);
        } catch (Exception e) {
            throw new RuntimeException("请求认证的参数不符合要求");
        }
        //从ioc里根据bean的名称获取bean
        String authType = authParamsDto.getAuthType();
        String beanName = authType + "_authService";
        AuthService authService = applicationContext.getBean(beanName, AuthService.class);
        XcUserExt execute = authService.execute(authParamsDto);
        UserDetails userPrincipal = getUserPrincipal(execute);
        return userPrincipal;
    }

    /**
     * @description 查询用户信息
     * @param user  用户id，主键
     * @return com.yaojiuye.ucenter.model.po.XcUser 用户信息
     * @author itnan
     */
    public UserDetails getUserPrincipal(XcUserExt user){
        //用户权限,如果不加报Cannot pass a null GrantedAuthority collection
        String[] authorities = {""};
        List<XcMenu> xcMenus = xcMenuMapper.selectPermissionByUserId(user.getId());
        if(!CollectionUtils.isEmpty(xcMenus)){
            List<String> permissions = new ArrayList<>();
            xcMenus.forEach(xcMenu -> {
                //拿到用户拥有的权限标识符
                permissions.add(xcMenu.getCode());
            });
            authorities = permissions.toArray(new String[0]);
        }
        String password = "";
        //为了安全在令牌中不放密码
        user.setPassword(null);
        //将user对象转json
        String userString = JSON.toJSONString(user);
        //创建UserDetails对象
        UserDetails userDetails = User.withUsername(userString).password(password).authorities(authorities).build();
        return userDetails;
    }
}
