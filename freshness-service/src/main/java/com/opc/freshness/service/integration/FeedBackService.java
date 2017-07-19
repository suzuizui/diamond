package com.opc.freshness.service.integration;

import com.opc.freshness.service.integration.domain.FeedBackUser;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * AUTHOR: qishang
 * DATE: 2017/7/19
 */
@FeignClient("com.opc.feedback.service.micro")
public interface FeedBackService {
    /**
     * 根据手机号查询用户信息
     *
     * @param mobile
     * @return
     */
    @RequestMapping(value = "/api/mc/user/queryMemberByMobile/v1", method = RequestMethod.GET)
    @ResponseBody
    FeedBackUser queryMemberByMobile(@RequestParam(value = "mobile") String mobile);

    /**
     * 根据用户编号查询
     *
     * @param userno
     * @return
     */
    @RequestMapping(value = "/api/mc/user/queryMemberByUserno/v1", method = RequestMethod.GET)
    @ResponseBody
    FeedBackUser queryMemberByUserno(@RequestParam(value = "userno") String userno);

    /**
     * 根据门店编号查询
     *
     * @param shopno
     * @return
     */
    @RequestMapping(value = "/api/mc/user/queryMembersByShopno/v1", method = RequestMethod.GET)
    @ResponseBody
    List<FeedBackUser> queryMembersByShopno(@RequestParam(value = "shopno") String shopno);

}
