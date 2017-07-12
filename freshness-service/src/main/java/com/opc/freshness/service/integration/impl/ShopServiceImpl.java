package com.opc.freshness.service.integration.impl;

import com.opc.freshness.service.integration.ShopService;
import com.wormpex.cvs.product.api.remote.ShopRemote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by qishang on 2017/7/12.
 */
@Service
public class ShopServiceImpl implements ShopService {
    private final static Logger logger = LoggerFactory.getLogger(ShopServiceImpl.class);
    @Resource
    private ShopRemote shopRemote;
}
