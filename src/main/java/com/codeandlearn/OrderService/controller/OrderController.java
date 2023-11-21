package com.codeandlearn.OrderService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codeandlearn.OrderService.exception.CustomException;
import com.codeandlearn.OrderService.model.OrderDetailsResponse;
import com.codeandlearn.OrderService.model.OrderRequest;
import com.codeandlearn.OrderService.service.OrderService;

import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/order")
@Log4j2
public class OrderController {

	@Autowired
	private OrderService orderService;
	
	//@PreAuthorize("hasAuthority('customer')")
	@PostMapping
	public ResponseEntity<Long> placeOrder(@RequestBody OrderRequest orderRequest){
		
		long orderId= orderService.placeOrder(orderRequest);
		log.info("Order Id: {}"+ orderId);
		return new ResponseEntity<Long>(orderId, HttpStatus.OK);
	}
	
	//@PreAuthorize("hasAuthority('customer') || hasAuthority('admin')")
	@GetMapping("/{id}")
	public ResponseEntity<OrderDetailsResponse> getOrderDetails(@PathVariable("id") long orderId) throws CustomException {
		log.info("===Order Id_1==: {}", orderId);
		return orderService.getOrderDetails(orderId);
	}
	
}
