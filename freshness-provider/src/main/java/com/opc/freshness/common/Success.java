package com.opc.freshness.common;

import lombok.Data;

/**
 * Created by perry on 4/18/17.
 */
@Data
public class Success<T> extends Result<T> {
    public Success(T data) {
        super(data);
    }
}