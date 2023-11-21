package com.codeandlearn.OrderService.external.decoder;

import com.codeandlearn.OrderService.external.client.exception.CustomExceptionExternal;
import com.fasterxml.jackson.databind.ObjectMapper;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class CustomErrorDecoder implements ErrorDecoder {

	@Override
	public Exception decode(String methodKey, Response response) {
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		try {
			
			CustomResponseEntityExternal customResponseEntity =	objectMapper.readValue(response.body().asInputStream(), CustomResponseEntityExternal.class);
			
			log.info("===SP_11== "+ customResponseEntity.getMsg()+"\n"+ customResponseEntity.getErrorCode()+"\n"+ response.status());
			
			return new CustomExceptionExternal(customResponseEntity.getMsg(), customResponseEntity.getErrorCode(), response.status());
		} catch (Exception e) {
			return new CustomExceptionExternal("Intternal Server Error", "INTERNAL_SERVER_ERROR", 500);
			
		}
	}

}
