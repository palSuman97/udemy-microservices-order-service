package com.codeandlearn.OrderService.external.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.codeandlearn.OrderService.exception.CustomException;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@CircuitBreaker(name = "external", fallbackMethod = "paymentFallBack" )
@FeignClient(name = "PAYMENT-SERVICE/payment")
public interface PaymentService {

	@PostMapping
	public long doPayment(@RequestBody PaymentRequest paymentRequest);

	default long paymentFallBack(PaymentRequest paymentRequest, Throwable th) throws CustomException {
		throw new CustomException("Payment Service is down!");
	}
}
