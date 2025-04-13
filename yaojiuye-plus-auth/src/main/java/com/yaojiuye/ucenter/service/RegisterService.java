package com.yaojiuye.ucenter.service;

import com.yaojiuye.base.model.RestResponse;
import com.yaojiuye.ucenter.model.dto.RegisterParamsDto;

public interface RegisterService {

    public RestResponse register(RegisterParamsDto registerParamsDto);
}
