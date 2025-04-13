package com.yaojiuye.checkcode.service;

import com.yaojiuye.checkcode.model.CheckCodeParamsDto;
import com.yaojiuye.checkcode.model.CheckCodeResultDto;

public interface SendCheckCodeService {

     CheckCodeResultDto sendCheckCodeByPhone(CheckCodeParamsDto checkCodeParamsDto);
}
