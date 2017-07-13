package com.opc.freshness.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class SkuKindsPo {
    private Integer id;

    private String name;

    private Float expired;

    private Integer delay;

    private String code;

    private Integer sort;
}