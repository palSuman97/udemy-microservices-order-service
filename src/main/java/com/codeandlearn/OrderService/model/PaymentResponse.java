package com.codeandlearn.OrderService.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentResponse {

	private long id;
	private String paymentMode;
	private String paymentStatus;
	private long referenceNumber;
}
