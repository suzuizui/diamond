package com.opc.freshness.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

/**
 * Created by perry on 5/9/17.
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> {
    private boolean ret;
    private T data;
    private String msg;
}
