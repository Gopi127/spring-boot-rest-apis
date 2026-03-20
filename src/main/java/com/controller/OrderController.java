package com.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dto.OrderRequestDto;
import com.dto.OrderResponseDto;
import com.service.OrderService;

@RestController
@RequestMapping("/orders")
public class OrderController {

	private final OrderService orderService;
	
	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}
	
	@PostMapping("/buy")
	public OrderResponseDto placeOrder(@RequestBody List<OrderRequestDto> orderRequestDtoList) {
		return orderService.placeOrder(orderRequestDtoList);
	}
}
