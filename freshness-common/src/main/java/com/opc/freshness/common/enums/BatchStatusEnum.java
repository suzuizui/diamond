package com.opc.freshness.common.enums;

/**
 * Created by qishang on 2017/7/13.
 */
public enum BatchStatusEnum {
    ZHZ(1, "准备中/回水中"),
    SMZ(2, "售卖中"),
    DFQ(3, "待废弃"),
    SK(4, "售空"),
    YFQ(5, "已废弃");


    private int status;
    private String name;

    BatchStatusEnum(int status, String name) {
        this.status = status;
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

}
