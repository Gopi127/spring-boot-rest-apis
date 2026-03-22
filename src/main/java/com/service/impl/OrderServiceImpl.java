package com.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.dao.OrderItemRepository;
import com.dao.OrderRepository;
import com.dao.ProductRepository;
import com.dto.OrderItemResponseDto;
import com.dto.OrderRequestDto;
import com.dto.OrderResponseDto;
import com.exceptions.OrderItemNotFoundException;
import com.exceptions.OrderNotFoundException;
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

		return buildOrderResponseDtoFromOrder(savedOrder);
	}

	private OrderResponseDto buildOrderResponseDtoFromOrder( Order savedOrder) {
		OrderResponseDto orderResponseDto = new OrderResponseDto();

		orderResponseDto.setOrderId(savedOrder.getOrderId());
		orderResponseDto.setStatus(savedOrder.getStatus());

		double totalAmount = 0;

		List<OrderItemResponseDto> orderItemResponseDtoList = new ArrayList<>();

		for (OrderItem orderItem : savedOrder.getOrderItems()) {
			OrderItemResponseDto orderItemResponseDto = new OrderItemResponseDto();

			orderItemResponseDto.setProductId(orderItem.getProduct().getProductId());
			orderItemResponseDto.setProductName(orderItem.getProduct().getProductName());
			orderItemResponseDto.setQuantity(orderItem.getQuantity());
			double eachProductPrice = orderItem.getProduct().getPrice() * ((100 - orderItem.getProduct().getDiscount()) / 100);
			orderItemResponseDto.setEachProductPrice(eachProductPrice);
			double totalProductPrice = eachProductPrice * orderItem.getQuantity();
			orderItemResponseDto.setTotalProductPrice(totalProductPrice);
			totalAmount += totalProductPrice;
			orderItemResponseDtoList.add(orderItemResponseDto);
		}

		orderResponseDto.setTotalAmount(totalAmount);
		orderResponseDto.setOrderItems(orderItemResponseDtoList);
		return orderResponseDto;
	}

	@Override
	public ResponseEntity<OrderResponseDto> getOrderInfo(long orderId) {
		
		Order order = orderRepository.findById(orderId)
					   .orElseThrow(() -> new OrderNotFoundException("No order found with id: " + orderId));
		
		
		OrderResponseDto orderResponseDto = buildOrderResponseDtoFromOrder(order);
		return ResponseEntity.status(HttpStatus.OK).body(orderResponseDto);
	}

	@Override
	public ResponseEntity<Void> cancelItem(long orderItemId) {
		
		OrderItem orderItem = orderItemRepository.findById(orderItemId)
						   .orElseThrow(() -> new OrderItemNotFoundException("No Order Item with Id: " + orderItemId));
							
		orderItemRepository.delete(orderItem);
		
		long productId = orderItem.getProduct().getProductId();
		int stock = orderItem.getProduct().getStock();
		productRepository.updateStock(productId, stock+orderItem.getQuantity());
		
		return ResponseEntity.noContent().build();
		
	}
	


}
