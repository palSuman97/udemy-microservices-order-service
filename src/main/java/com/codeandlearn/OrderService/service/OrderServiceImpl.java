package com.codeandlearn.OrderService.service;


import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.codeandlearn.OrderService.entity.Order;
import com.codeandlearn.OrderService.exception.CustomException;
import com.codeandlearn.OrderService.external.client.PaymentRequest;
import com.codeandlearn.OrderService.external.client.PaymentService;
import com.codeandlearn.OrderService.external.client.ProductService;
import com.codeandlearn.OrderService.model.OrderDetailsResponse;
import com.codeandlearn.OrderService.model.OrderRequest;
import com.codeandlearn.OrderService.model.PaymentResponse;
import com.codeandlearn.OrderService.model.ProductResponse;
import com.codeandlearn.OrderService.repository.OrderRepository;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private PaymentService paymentService;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Override
	public long placeOrder(OrderRequest orderRequest) {
		
		//OrderService -> Save the data with order status created
		//ProductService -> Block Products(Reduce Quantity)
		productService.reduceQuantiny(orderRequest.getProductId(), orderRequest.getQuantity());
		
		//PaymentService -> payment--> success-->complete or CANCELLED
		
		log.info("placing order: {}"+ orderRequest);
		
		Order order= Order.builder()
				.productId(orderRequest.getProductId())
				.quantity(orderRequest.getQuantity())
				.amount(1234)
				.orderDate(Instant.now())
				.orderStatus("CREATED")
				.build();
			
		order= orderRepository.save(order);
		
		log.info("calling payment-service");
		
		PaymentRequest paymentRequest= PaymentRequest.builder()
										.orderId(order.getOrderId())
										.paymentMode(orderRequest.getPaymentMode())
										.amount(1400)
										.build();
		
		String orderStatus=null;
		if((Long)paymentService.doPayment(paymentRequest) instanceof Long) {
			paymentService.doPayment(paymentRequest);
			log.info("order placed successfully");
			orderStatus="PLACED";
		}
		else {
			log.error("Error Occurred in payment. order not placed");
			orderStatus="PAYMENT_FAILED";
		}
		order.setOrderStatus(orderStatus);
		orderRepository.save(order);
		
		log.info("Order Placed successfully with OrderId: {}"+ order.getOrderId());
		
		return order.getOrderId();
	}

	@Override
	public ResponseEntity<OrderDetailsResponse> getOrderDetails(long orderId) throws CustomException {
		
		log.info("===orderId_1== {} ", orderId);
		
		Order order= orderRepository.findById(orderId).orElseThrow(()-> new CustomException("Order Id not found"));
		
		log.info("===order_1== {}", order);
		
		log.info("calling product service to get required details");
		
		ProductResponse product= restTemplate.getForObject("http://PRODUCT-SERVICE/product/"+ order.getProductId(), ProductResponse.class);
		
		OrderDetailsResponse.ProductModel productmodel= OrderDetailsResponse.ProductModel.builder()
															.productId(product.getProductId())
															.productName(product.getProductName())
															.price(product.getPrice())
															.build();
		
		log.info("calling payment service to get required details");
		
		PaymentResponse payment= restTemplate.getForObject("http://PAYMENT-SERVICE/payment/"+ order.getOrderId(), PaymentResponse.class);
		
		log.info("===payment== {} ", payment);
		
		
		OrderDetailsResponse.PaymentDetails paymentDetails= OrderDetailsResponse.PaymentDetails.builder()
																.id(payment.getId())
																.paymentMode(payment.getPaymentMode())
																.paymentStatus(payment.getPaymentStatus())
																.referenceNumber(payment.getReferenceNumber())
																.build();
		
		OrderDetailsResponse orderDetailsResponse= OrderDetailsResponse.builder()
				.orderId(order.getOrderId())
				.orderDate(order.getOrderDate())
				.orderStatus(order.getOrderStatus())
				.quantity(order.getQuantity())
				.productModel(productmodel)
				.paymentDetails(paymentDetails)
				.build();
		
		return new ResponseEntity<OrderDetailsResponse>(orderDetailsResponse, HttpStatus.OK);
	}

}
