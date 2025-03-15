package com.yaojiuye.base.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    /**
     * 针对自定义异常处理
     * @param e
     * @return
     */
    @ExceptionHandler(GlobalException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse customException(GlobalException e) {
        log.error("【系统异常】{}", e.getErrMessage());
        return new RestErrorResponse(e.getErrMessage());
    }

    /**
     * 针对系统异常处理
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse exception(Exception e) {
        log.error("【系统异常】{}",e.getMessage(),e);
        return new RestErrorResponse(CommonError.UNKOWN_ERROR.getErrMessage());
    }

    /**
     * 针对JSR-303校验异常处理
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse methodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        //错误信息集合
        List<String> errorList = new ArrayList();
        bindingResult.getFieldErrors().forEach(fieldError -> {
            errorList.add(fieldError.getDefaultMessage());
        });
        //将errorList转为字符串中间用逗号隔开这些错误信息
        String errorMessage = String.join(",", errorList);

        return new RestErrorResponse(errorMessage);
    }
}
