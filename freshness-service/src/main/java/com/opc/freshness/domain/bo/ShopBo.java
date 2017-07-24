package com.opc.freshness.domain.bo;

import lombok.Data;

/**
 * AUTHOR: qishang
 * DATE: 2017/7/24
 */
@Data
public class ShopBo {
    private Integer id;
    private Integer shopId;
    private String shopCode;
    private PropInfo propInfo;
    private BizInfo bizInfo;
    @Data
    class PropInfo {
        private String name;
        private String displayName;
    }
    @Data
    class BizInfo{
        private Integer shopType;
    }
}
