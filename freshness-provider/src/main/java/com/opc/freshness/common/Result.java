package com.opc.freshness.common;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by perry on 5/9/17.
 */
@Getter
@Setter
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> {
	private boolean ret;
	private T data;
	private String msg;

	public Result(T data) {
		this.ret = true;
		this.data = data;
	}

	public Result(String msg) {
		this.ret = false;
		this.msg = msg;
	}
}
