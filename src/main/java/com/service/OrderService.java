package com.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.dto.OrderRequestDto;
import com.dto.OrderResponseDto;

public interface OrderService {

	OrderResponseDto placeOrder(List<OrderRequestDto> orderRequestDtoList);

	ResponseEntity<OrderResponseDto> getOrderInfo(long orderId);

	ResponseEntity<Void> cancelItem(long orderItemId);

}
