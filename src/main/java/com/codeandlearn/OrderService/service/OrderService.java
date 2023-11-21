package com.codeandlearn.OrderService.service;

import org.springframework.http.ResponseEntity;

import com.codeandlearn.OrderService.exception.CustomException;
import com.codeandlearn.OrderService.model.OrderDetailsResponse;
import com.codeandlearn.OrderService.model.OrderRequest;

public interface OrderService {

	long placeOrder(OrderRequest orderRequest);

	ResponseEntity<OrderDetailsResponse> getOrderDetails(long orderId) throws CustomException;

}
