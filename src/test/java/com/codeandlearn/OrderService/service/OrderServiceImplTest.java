package com.codeandlearn.OrderService.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.codeandlearn.OrderService.entity.Order;
import com.codeandlearn.OrderService.exception.CustomException;
import com.codeandlearn.OrderService.external.client.PaymentRequest;
import com.codeandlearn.OrderService.external.client.PaymentService;
import com.codeandlearn.OrderService.external.client.ProductService;
import com.codeandlearn.OrderService.model.OrderDetailsResponse;
import com.codeandlearn.OrderService.model.OrderRequest;
import com.codeandlearn.OrderService.model.PaymentMode;
import com.codeandlearn.OrderService.model.PaymentResponse;
import com.codeandlearn.OrderService.model.ProductResponse;
import com.codeandlearn.OrderService.repository.OrderRepository;

@SpringBootTest
class OrderServiceImplTest {

	@Mock
	private OrderRepository orderRepository;
	
	@Mock
	private ProductService productService;
	
	@Mock
	private PaymentService paymentService;
	
	@Mock
	private RestTemplate restTemplate;
	
	@InjectMocks
	OrderService orderService = new OrderServiceImpl();
	
	@DisplayName("Get Order Details - Success Scenario")
	@Test
	void test_When_Order_Success() throws CustomException {
		
		//Mocking
		
		Order order = getMockOrder();
		
		when(orderRepository.findById(anyLong()))
			.thenReturn(Optional.of(order));
		
		when(restTemplate.getForObject("http://PRODUCT-SERVICE/product/"+ order.getProductId(), ProductResponse.class))
			.thenReturn(getMockProductResponse());
		
		when(restTemplate.getForObject("http://PAYMENT-SERVICE/payment/"+ order.getOrderId(), PaymentResponse.class))
			.thenReturn(getMockPaymentResponse());
		
		//Actual
		ResponseEntity<OrderDetailsResponse> orderDetailsResponse = orderService.getOrderDetails(3);
		//Verifying
		
		verify(orderRepository, times(1)).findById(anyLong());
		verify(restTemplate, times(1)).getForObject("http://PRODUCT-SERVICE/product/"+ order.getProductId(), ProductResponse.class);
		verify(restTemplate, times(1)).getForObject("http://PAYMENT-SERVICE/payment/"+ order.getOrderId(), PaymentResponse.class);
		
		//Assert
		Assertions.assertNotNull(orderDetailsResponse.getBody());
		Assertions.assertEquals(1, orderDetailsResponse.getBody().getOrderId());
	}
	
	@DisplayName("Get Order Details - Failure Scenario")
	@Test
	void test_when_order_not_found() {
		
		when(orderRepository.findById(anyLong()))
			.thenReturn(Optional.ofNullable(null));
		
		CustomException exception = assertThrows(CustomException.class, () -> orderService.getOrderDetails(1));
		assertEquals("Order Id not found", exception.getMessage());
		verify(orderRepository, times(1)).findById(anyLong());
	}
	
	private Order getMockOrder() { 
		
		return Order.builder()
						.amount(1200)
						.orderId(1)
						.orderStatus("PLACED")
						.productId(2)
						.quantity(1)
						.orderDate(Instant.now())
						.build();
	}
	
	@DisplayName("Place Order - Success Scenario")
	@Test
	void test_placeOrder_success() {
		
		//Mocking
		Order order = getMockOrder();
		OrderRequest orderRequest = getMockOrderRequest();
		
		when(productService.reduceQuantiny(anyLong(), anyLong()))
			.thenReturn("ok");
		when(orderRepository.save(any(Order.class))).thenReturn(order);
		when(paymentService.doPayment(getMockPaymentRequest()))
			.thenReturn(1L);
		
		//Actual
		long orderId = orderService.placeOrder(orderRequest);
		
		//verifying
		verify(orderRepository, times(2)).save(any());
		verify(productService, times(1)).reduceQuantiny(anyLong(), anyLong());
		verify(paymentService, times(2)).doPayment(any(PaymentRequest.class));
		
		assertEquals(order.getOrderId(), orderId);
		
	}
	
	@DisplayName("Place Order - Failure Scenario")
	@Test
	void test_placeOrder_failure() {
		
		//Mocking
		Order order = getMockOrder();
		OrderRequest orderRequest = getMockOrderRequest();
		
		when(productService.reduceQuantiny(anyLong(), anyLong()))
			.thenReturn("ok");
		
		when(orderRepository.save(any(Order.class))).thenReturn(order);
		
		when(paymentService.doPayment(getMockPaymentRequest()))
			.thenThrow(new RuntimeException());
		
		//Actual
		long orderId = orderService.placeOrder(orderRequest);
		
		//verifying
		verify(orderRepository, times(2)).save(any());
		verify(productService, times(1)).reduceQuantiny(anyLong(), anyLong());
		verify(paymentService, times(2)).doPayment(any(PaymentRequest.class));
		
		assertEquals(order.getOrderId(), orderId);
	}
	
	private PaymentRequest getMockPaymentRequest() {
		
		return PaymentRequest.builder()
				.orderId(1)
				.paymentMode(PaymentMode.UPI)
				.amount(1200)
				.build();
	}

	private OrderRequest getMockOrderRequest() {
		
		return OrderRequest.builder()
				.productId(2)
				.paymentMode(PaymentMode.UPI)
				.quantity(10)
				.build();
	}

	private ProductResponse getMockProductResponse() {
		return ProductResponse.builder()
				.price(100)
				.productId(2)
				.productName("Samsung")
				.quantity(100)
				.build();
	}
	
	private PaymentResponse getMockPaymentResponse() {
		return PaymentResponse.builder()
				.id(2)
				.paymentMode("UPI")
				.paymentStatus("SUCCESSFUL")
				.referenceNumber(904994)
				.build();	
	}
}
