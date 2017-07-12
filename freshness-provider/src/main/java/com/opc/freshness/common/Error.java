package com.opc.freshness.common;

import lombok.Data;

/**
 * Created by perry on 4/18/17.
 */
@Data
public class Error<T> extends Result<T> {

	public final static int ERROR_CODE_LOGIN = 1001;

	public Error(String msg) {
		super(msg);
	}

	public Error(int code, String msg) {
		super(code, msg);
	}
}
