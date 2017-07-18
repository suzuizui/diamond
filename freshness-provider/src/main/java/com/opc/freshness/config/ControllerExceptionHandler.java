package com.opc.freshness.config;

import com.opc.freshness.common.Error;
import com.opc.freshness.common.Result;
import com.wormpex.biz.BizException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

/**
 * AUTHOR: qishang
 * DATE: 2017/7/12
 */
@ControllerAdvice
public  final class ControllerExceptionHandler {
    private final static Logger logger = LoggerFactory.getLogger(ControllerExceptionHandler.class);
    /**
     * 基于@ExceptionHandler异常处理
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody Result exp(HttpServletRequest request, Exception e) {
        logger.error("系统异常", e);
        if (e instanceof BizException) {
            return new Error(StringUtils.isEmpty(e.getMessage()) ? "系统异常" : e.getMessage());
        }
        return new Error("系统异常");
    }
}
