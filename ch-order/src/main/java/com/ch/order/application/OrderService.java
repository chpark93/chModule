package com.ch.order.application;

import com.ch.order.domain.Order;
import com.ch.order.dto.order.OrderDto;
import com.ch.order.dto.order.response.OrderResponse;

public interface OrderService {

    OrderResponse createOrder(OrderDto orderDto);

    OrderResponse getOrderByOrderId(Long orderId);

    Iterable<Order> getOrderByUserId(String userId);
}
