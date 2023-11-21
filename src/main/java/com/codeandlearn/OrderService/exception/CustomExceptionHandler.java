package com.codeandlearn.OrderService.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.codeandlearn.OrderService.external.client.exception.CustomExceptionExternal;
import com.codeandlearn.OrderService.external.decoder.CustomResponseEntityExternal;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(CustomException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public CustomResponseEntity errorMsg(CustomException customException) {
		
		CustomResponseEntity errorMsg= 
				new CustomResponseEntity(HttpStatus.BAD_REQUEST, customException.getMessage());
		return errorMsg;
	}
	
	@ExceptionHandler(CustomExceptionExternal.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public CustomResponseEntityExternal errorMsg(CustomExceptionExternal customExceptionExternal) {
		
		CustomResponseEntityExternal errorMsg = 
				new CustomResponseEntityExternal(customExceptionExternal.getMessage(), customExceptionExternal.getErrorCode(), customExceptionExternal.getErrorStatus());
		return errorMsg;
	}
}
