package com.codeandlearn.OrderService.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomResponseEntity {

	private HttpStatus httpStatus;
	private String msg;
}
