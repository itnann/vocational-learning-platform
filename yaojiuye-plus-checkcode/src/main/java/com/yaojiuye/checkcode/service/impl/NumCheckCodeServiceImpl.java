package com.yaojiuye.checkcode.service.impl;

import com.yaojiuye.checkcode.model.CheckCodeParamsDto;
import com.yaojiuye.checkcode.model.CheckCodeResultDto;
import com.yaojiuye.checkcode.service.AbstractCheckCodeService;
import com.yaojiuye.checkcode.service.CheckCodeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author itnan
 * @ClassName NumCheckCodeServiceImpl
 * @Description 数字验证码生成器
 * @Date 2025/4/12 21:34
 * @Version V1.0
 */
@Service("NumCheckCodeService")
public class NumCheckCodeServiceImpl extends AbstractCheckCodeService implements CheckCodeService {

    @Resource(name="NumberCheckCodeGenerator")
    @Override
    public void setCheckCodeGenerator(CheckCodeGenerator checkCodeGenerator) {
        this.checkCodeGenerator = checkCodeGenerator;
    }

    @Resource(name="UUIDKeyGenerator")
    @Override
    public void setKeyGenerator(KeyGenerator keyGenerator) {
        this.keyGenerator = keyGenerator;
    }


    @Resource(name="RedisCheckCodeStore")
    @Override
    public void setCheckCodeStore(CheckCodeStore checkCodeStore) {
        this.checkCodeStore = checkCodeStore;
    }

    @Override
    public CheckCodeResultDto generate(CheckCodeParamsDto checkCodeParamsDto) {
        GenerateResult generate = generate(checkCodeParamsDto, 4, "checkcode:", 300);
        String key = generate.getKey();
        String code = generate.getCode();
        CheckCodeResultDto checkCodeResultDto = new CheckCodeResultDto();
        checkCodeResultDto.setAliasing(code);
        checkCodeResultDto.setKey(key);
        return checkCodeResultDto;

    }
}
