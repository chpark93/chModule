package com.ch.order.application.impl;

import com.ch.core.code.Errors;
import com.ch.core.exception.BusinessException;
import com.ch.order.application.OrderService;
import com.ch.order.domain.Order;
import com.ch.order.dto.order.OrderDto;
import com.ch.order.dto.order.response.OrderResponse;
import com.ch.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public OrderResponse createOrder(OrderDto orderDto) {
        orderDto.setTotalPrice(orderDto.getQty() * orderDto.getUnitPrice());

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Order order = modelMapper.map(orderDto, Order.class);

        orderRepository.save(order);

        return modelMapper.map(order, OrderResponse.class);
    }

    @Override
    public OrderResponse getOrderByOrderId(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(Errors.BAD_REQUEST.getMessage(), Errors.BAD_REQUEST));

        return new ModelMapper().map(order, OrderResponse.class);
    }

    @Override
    public Iterable<Order> getOrderByUserId(String userId) {
        return orderRepository.findByUserId(userId);
    }
}
