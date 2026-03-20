package com.service;

import java.util.List;

import com.dto.OrderRequestDto;
import com.dto.OrderResponseDto;

public interface OrderService {

	OrderResponseDto placeOrder(List<OrderRequestDto> orderRequestDtoList);

}
