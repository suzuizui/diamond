package com.opc.freshness.common;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by perry on 4/18/17.
 */
@Getter
@Setter
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Success<T> extends Result<T> {
	public Success(T data) {
		super(data);
	}
}