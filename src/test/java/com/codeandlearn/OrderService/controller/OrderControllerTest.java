package com.codeandlearn.OrderService.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.StreamUtils;

import com.codeandlearn.OrderService.entity.Order;
import com.codeandlearn.OrderService.model.OrderRequest;
import com.codeandlearn.OrderService.model.PaymentMode;
import com.codeandlearn.OrderService.repository.OrderRepository;
import com.codeandlearn.OrderService.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;

@SpringBootTest({"server.port=0"})
@EnableConfigurationProperties
@AutoConfigureMockMvc
@ContextConfiguration(classes = OrderServiceConfig.class)
class OrderControllerTest {

	@Autowired
	private OrderService orderService;
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private MockMvc mockMvc;
	
	@RegisterExtension
	static WireMockExtension wireMockServer 
		= WireMockExtension.newInstance()
		.options(WireMockConfiguration
				.wireMockConfig()
				.port(8080))
		.build();
	
	private ObjectMapper objectMapper 
		= new ObjectMapper()
		.findAndRegisterModules()
		.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
		.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	@BeforeEach
	void setup() throws IOException {
		getProductDetailsResponse();
		doPayment();
		getPaymentDetails();
		reduceQuantity();
	}
	
	
	private void reduceQuantity() {
		wireMockServer.stubFor(WireMock.put(WireMock.urlMatching("/product/reduceQuantity/.*"))
				.willReturn(WireMock.aResponse()
						.withStatus(HttpStatus.OK.value())
						.withHeader("content-type", MediaType.APPLICATION_JSON_VALUE)));
		
		
	}


	private void getPaymentDetails() throws IOException {
		
		wireMockServer.stubFor(WireMock.get(WireMock.urlMatching("/payment/.*"))
				.willReturn(WireMock.aResponse()
						.withStatus(HttpStatus.OK.value())
						.withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
						.withBody(StreamUtils
								.copyToString(
										OrderControllerTest.class
										.getClassLoader()
										.getResourceAsStream("mock/GetPaymentDetails.json"), 
										Charset.defaultCharset()))));	
	}


	private void doPayment() {
		
		wireMockServer.stubFor(WireMock.post(WireMock.urlEqualTo("*/payment*"))
				.willReturn(WireMock.aResponse()
						.withStatus(HttpStatus.OK.value())
						.withHeader("content-type", MediaType.APPLICATION_JSON_VALUE)));
		
	}


	private void getProductDetailsResponse() throws IOException {
		
		wireMockServer.stubFor(WireMock.get("/product/1")
				.willReturn(WireMock.aResponse()
						.withStatus(HttpStatus.OK.value())
						.withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
						.withBody(StreamUtils.copyToString(
								OrderControllerTest.class
								.getClassLoader()
								.getResourceAsStream("mock/GetProduct.json"),
								Charset.defaultCharset()
								))));
		
	}


	@Test
	void test_WhenPlaceOrder_DoPayment_Success() throws JsonProcessingException, Exception {
		//First Place Order
		//Get Order Id from DB & check
		//Check Output
		
		OrderRequest orderRequest = getMockOrderRequest();
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/order")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsString(orderRequest))
				).andExpect(MockMvcResultMatchers.status().isOk())
		.andReturn();
		
		String orderId = mvcResult.getResponse().getContentAsString();
		
		Optional<Order> order = orderRepository.findById(Long.valueOf(orderId));
		assertTrue(order.isPresent());
		
		Order o = order.get();
		assertEquals(Long.parseLong(orderId), o.getOrderId());
		assertEquals("PLACED", o.getOrderStatus());
		assertEquals(orderRequest.getQuantity(), o.getQuantity());
	}


	private OrderRequest getMockOrderRequest() {
		
		return OrderRequest.builder()
				.productId(1)
				.paymentMode(PaymentMode.UPI)
				.quantity(100)
				.build();
	}
	
	
}
