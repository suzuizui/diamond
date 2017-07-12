package com.opc.freshness.service.integration.impl;

import com.opc.freshness.service.integration.ProductService;
import com.wormpex.cvs.product.api.remote.ProductRemote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

/**
 * Created by qishang on 2017/7/12.
 */
public class ProductServiceImpl implements ProductService {
    private final static Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);
    @Resource
    private ProductRemote productRemote;

}
