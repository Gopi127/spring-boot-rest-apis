package com.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dao.OrderItemRepository;
import com.dao.OrderRepository;
import com.dao.ProductRepository;
import com.dto.OrderItemResponseDto;
import com.dto.OrderRequestDto;
import com.dto.OrderResponseDto;
import com.model.Order;
import com.model.OrderItem;
import com.model.Product;
import com.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	ProductRepository productRepository;

	@Autowired
	OrderRepository orderRepository;

	@Autowired
	OrderItemRepository orderItemRepository;

	@Override
	public OrderResponseDto placeOrder(List<OrderRequestDto> orderRequestDtoList) {

		Order order = new Order();

		List<OrderItem> orderItemsList = new ArrayList<>();

		order.setStatus("Ordered");

		for (OrderRequestDto orderRequestDto : orderRequestDtoList) {
			OrderItem orderItem = new OrderItem();

			Product product = productRepository.findById(orderRequestDto.getProductId()).get();

			if (product.getStock() >= orderRequestDto.getQuantity()) {
				orderItem.setQuantity(orderRequestDto.getQuantity());
				orderItem.setOrder(order);
				orderItem.setProduct(product);
				orderItemsList.add(orderItem);
				productRepository.updateStock(product.getProductId(),
						product.getStock() - orderRequestDto.getQuantity());
			}
		}
		order.setOrderItems(orderItemsList);

		Order savedOrder = orderRepository.save(order);

		OrderResponseDto orderResponseDto = new OrderResponseDto();

		orderResponseDto.setOrderId(savedOrder.getOrderId());
		orderResponseDto.setStatus(order.getStatus());

		double totalAmount = 0;

		List<OrderItemResponseDto> orderItemResponseDtoList = new ArrayList<>();

		for (OrderItem orderItem : order.getOrderItems()) {
			OrderItemResponseDto orderItemResponseDto = new OrderItemResponseDto();

			orderItemResponseDto.setProductId(orderItem.getProduct().getProductId());
			orderItemResponseDto.setProductName(orderItem.getProduct().getProductName());
			orderItemResponseDto.setQuantity(orderItem.getQuantity());
			orderItemResponseDto.setEachProductPrice(orderItem.getProduct().getPrice());
			double totalProductPrice = orderItem.getProduct().getPrice() * orderItem.getQuantity();
			orderItemResponseDto.setTotalProductPrice(totalProductPrice);
			totalAmount += totalProductPrice;
			orderItemResponseDtoList.add(orderItemResponseDto);
		}

		orderResponseDto.setTotalAmount(totalAmount);
		orderResponseDto.setOrderItems(orderItemResponseDtoList);
		return orderResponseDto;
	}

}
