package com.ch.order.api;

import com.ch.order.application.OrderService;
import com.ch.order.domain.Order;
import com.ch.order.dto.order.OrderDto;
import com.ch.order.dto.order.request.OrderRequest;
import com.ch.order.dto.order.response.OrderResponse;
import com.ch.order.queue.KafkaProducer;
import com.ch.order.queue.OrderProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OrderApi {

    private final OrderService orderService;
    private final KafkaProducer kafkaProducer;
    private final OrderProducer orderProducer;

    @PostMapping("/{userId}/orders")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest orderRequest, @PathVariable("userId") String userId) {
        log.info("Before Add Orders Data");

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        OrderDto orderDto = modelMapper.map(orderRequest, OrderDto.class);
        orderDto.setUserId(userId);

        // Kafka
        orderDto.setTotalPrice(orderRequest.getQty() * orderRequest.getUnitPrice());

        // Send Kafka Message - Order
        kafkaProducer.sendOrder("ch-catalog-topic", orderDto);
        orderProducer.sendOrder("orders", orderDto);

        // Response
        OrderResponse orderResponse = modelMapper.map(orderDto, OrderResponse.class);

        log.info("After Added Orders Data");

        return ResponseEntity.status(HttpStatus.CREATED).body(orderResponse);
    }

    @GetMapping("/{userId}/orders")
    public ResponseEntity<List<OrderResponse>> getOrders(@PathVariable("userId") String userId) {
        log.info("Before Retrieve Orders Data");
        Iterable<Order> orderList = orderService.getOrderByUserId(userId);

        List<OrderResponse> result = new ArrayList<>();
        orderList.forEach(info -> result.add(new ModelMapper().map(info, OrderResponse.class)));

        log.info("Add Retrieved Orders Data");

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }



}
