package com.opc.freshness.utils;

import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * AUTHOR: qishang
 * DATE: 2017/7/24
 */
public class HttpClientUtils {
    RestTemplate template = new RestTemplate();

    public String HttpGetJson(String url, Map paramMap) {
        return template.getForObject(url, String.class);
    }
}
