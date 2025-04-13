package com.yaojiuye.ucenter.service;

import com.yaojiuye.base.model.RestResponse;
import com.yaojiuye.ucenter.model.dto.FindPasswordParamsDto;

public interface FindPasswordService {

    RestResponse findPassword(FindPasswordParamsDto findPasswordParamsDto);
}
