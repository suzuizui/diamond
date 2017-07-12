package com.opc.freshness.common;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by perry on 5/9/17.
 */
@Data
public class Result<T> implements Serializable {
	private boolean ret;
	private T data;
	private int code = -1;
	private String msg;

	public Result(T data) {
		this.ret = true;
		this.code = 0;
		this.data = data;
	}

	public Result(String msg) {
		this.ret = false;
		this.msg = msg;
	}

	public Result(int code, String msg) {
		this.ret = false;
		this.code = code;
		this.msg = msg;
	}
}
