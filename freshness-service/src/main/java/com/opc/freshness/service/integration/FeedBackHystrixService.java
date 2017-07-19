package com.opc.freshness.service.integration;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.opc.freshness.service.integration.domain.FeedBackUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AUTHOR: qishang
 * DATE: 2017/7/19
 */
@Service
public class FeedBackHystrixService {

    public static final Logger logger = LoggerFactory.getLogger(FeedBackHystrixService.class);

    @Autowired
    private FeedBackService service;

    @HystrixCommand(fallbackMethod = "fallbackMethod")
    public FeedBackUser queryMemberByMobile(String mobile) {
        return this.service.queryMemberByMobile(mobile);
    }

    @HystrixCommand(fallbackMethod = "fallbackMethod")
    public FeedBackUser queryMemberByUserno(String userno) {
        return this.service.queryMemberByUserno(userno);
    }

    @HystrixCommand(fallbackMethod = "queryMembersFallback")
    public List<FeedBackUser> queryMembersByShopno(String shopno) {
        return this.service.queryMembersByShopno(shopno);
    }

    public FeedBackUser fallbackMethod(String param, Throwable e) {
        logger.error("excute fallbackMethod,param : " + param, e);
        return null;
    }

    public List<FeedBackUser> queryMembersFallback(String shopno, Throwable e) {
        logger.error("excute fallbackMethod,shopno : " + shopno, e);
        return null;
    }

}