package com.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {
	
	private long orderId;
	
	private String status;
	
	private double totalAmount;
	
	private List<OrderItemResponseDto> orderItems;

}
