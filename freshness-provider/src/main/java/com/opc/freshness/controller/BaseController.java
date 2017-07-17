package com.opc.freshness.controller;

import com.ctc.wstx.util.StringUtil;
import com.opc.freshness.common.Error;
import com.opc.freshness.common.Result;
import com.wormpex.biz.BizException;
import com.wormpex.biz.lang.Biz;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by qishang on 2017/7/12.
 */
public class BaseController {
    private final static Logger logger = LoggerFactory.getLogger(BaseController.class);

    /**
     * 基于@ExceptionHandler异常处理
     */
    @ExceptionHandler
    public Result exp(HttpServletRequest request, Exception e) {
        logger.error("", e);
        if (e instanceof BizException) {
            return new Error(StringUtils.isEmpty(e.getMessage()) ? "系统异常" : e.getMessage());
        }
        return new Error("系统异常");
    }
}
