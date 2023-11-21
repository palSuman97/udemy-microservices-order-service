package com.codeandlearn.OrderService.external.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.codeandlearn.OrderService.exception.CustomException;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@CircuitBreaker(name = "external", fallbackMethod = "productFallBack")
@FeignClient(name = "PRODUCT-SERVICE/product")
public interface ProductService {
	
	@PutMapping("/reduceQuantity/{id}")
	public String reduceQuantiny(@PathVariable("id") long productId, @RequestParam("quantity") long ordered_quantity);
	
	default String productFallBack(long l, long lo, Throwable th) throws CustomException {
		System.out.println("th-- "+ th.getMessage());
		throw new CustomException(th.getMessage());
	}

}
