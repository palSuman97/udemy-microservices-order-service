package com.codeandlearn.OrderService.model;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailsResponse {

	private long orderId;
	private Instant orderDate;
	private long quantity;
	private String orderStatus;
	private ProductModel productModel;
	private PaymentDetails paymentDetails;
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class ProductModel {
		
		private long productId;
		private String productName;
		private long price;
	}
	
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	public static class PaymentDetails {

		private long id;
		private String paymentMode;
		private String paymentStatus;
		private long referenceNumber;
	}

}
