package com.yaojiuye.base.exception;


/**
 * @description 要就业项目异常类
 * @version 1.0
 */
public class GlobalException extends RuntimeException {

    private String errMessage;

    public GlobalException() {
        super();
    }

    public GlobalException(String errMessage) {
        super(errMessage);
        this.errMessage = errMessage;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public static void cast(CommonError commonError){
        throw new GlobalException(commonError.getErrMessage());
    }
    public static void cast(String errMessage){
        throw new GlobalException(errMessage);
    }

}