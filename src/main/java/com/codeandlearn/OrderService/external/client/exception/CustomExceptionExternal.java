package com.codeandlearn.OrderService.external.client.exception;

import lombok.Data;

@Data
public class CustomExceptionExternal extends RuntimeException {

	private String errorCode;
	private int errorStatus;
	
	public CustomExceptionExternal(String msg, String string, int errorStatus) {
		super(msg);
		this.errorCode = string;
		this.errorStatus = errorStatus;
	}
}
