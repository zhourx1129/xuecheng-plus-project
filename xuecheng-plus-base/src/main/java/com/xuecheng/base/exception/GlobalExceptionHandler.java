package com.xuecheng.base.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;

/**
 * @Author: zhourx
 * @Description:
 * @Date: 2023/9/17
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
//    对项目的自定义异常进行处理
    @ResponseBody
    @ExceptionHandler(XueChengPlusException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse customException(XueChengPlusException e){

//        记录异常
        log.error("系统异常:{}",e.getErrMessage(),e);
//        解析出异常信息
        String errMessage = e.getErrMessage();
        RestErrorResponse restErrorResponse = new RestErrorResponse(errMessage);
        return restErrorResponse;
    }
    //    对项目的自定义异常进行处理
    @ResponseBody
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse exception(Exception e){

//        记录异常
        log.error("系统异常:{}",e.getMessage(),e);
//        解析出异常信息
        RestErrorResponse restErrorResponse = new RestErrorResponse(CommonError.UNKOWN_ERROR.getErrMessage());
        return restErrorResponse;
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse methodArgumentNoValidException(MethodArgumentNotValidException e){
        BindingResult bindingResult = e.getBindingResult();
        //存放错误信息
        ArrayList<String> errList = new ArrayList<>();
        bindingResult.getFieldErrors().stream().forEach(item -> {
            errList.add(item.getDefaultMessage());
        });
        //将list中的错误信息拼接起来
        String errMessage = StringUtils.join(errList, ",");
//        记录异常
        log.error("系统异常:{}",e.getMessage(),errMessage);
//        解析出异常信息
        RestErrorResponse restErrorResponse = new RestErrorResponse(errMessage);
        return restErrorResponse;
    }
}
