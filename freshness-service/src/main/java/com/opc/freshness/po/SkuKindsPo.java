package com.opc.freshness.po;

import lombok.Data;

@Data
public class SkuKindsPo {
    private Integer id;

    private String name;

    private Float expired;

    private Integer delay;
}