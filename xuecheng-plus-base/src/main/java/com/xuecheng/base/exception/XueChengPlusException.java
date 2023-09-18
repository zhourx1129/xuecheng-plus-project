package com.xuecheng.base.exception;

import java.security.UnresolvedPermission;

/**
 * @Author: zhourx
 * @Description:  本项目自定义异常信息模型
 * @Date: 2023/9/17
 */
public class XueChengPlusException extends RuntimeException{
    private String errMessage;

    public XueChengPlusException() {
        super();
    }

    public XueChengPlusException(String errMessage) {
        super(errMessage);
        this.errMessage = errMessage;
    }
    public String getErrMessage() {
        return errMessage;
    }

    public static void cast(CommonError commonError){
        throw new XueChengPlusException(commonError.getErrMessage());
    }
    public static void cast(String errMessage){
        throw new XueChengPlusException(errMessage);
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }
}
