package com.xuecheng.base.exception;

/**
 * @Author: zhourx
 * @Description: 和前端约定，返回异常的信息模型
 * @Date: 2023/9/17
 */
public class RestErrorResponse {

    private String errMessage;

    public RestErrorResponse(String errMessage){
        this.errMessage= errMessage;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }

}
