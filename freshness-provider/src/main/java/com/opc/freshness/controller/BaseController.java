package com.opc.freshness.controller;

import com.opc.freshness.common.Error;
import com.opc.freshness.common.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by qishang on 2017/7/12.
 */
public class BaseController {
    /**
     * 基于@ExceptionHandler异常处理
     */
    @ExceptionHandler
    public Result exp(HttpServletRequest request, Exception e) {
        return new Error(e.getMessage());
    }
}
