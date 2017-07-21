package com.opc.freshness.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * AUTHOR: qishang
 * DATE: 2017/7/21
 */
public class ContactDeviceConfig {
    private static final Map<String, List<String>> config = new HashMap<>();

    static {
        config.put("", Stream.of("").collect(Collectors.toList()));
    }

    public static  List<String> getConfig(String deviceId) {
        return config.get(deviceId);
    }
}
