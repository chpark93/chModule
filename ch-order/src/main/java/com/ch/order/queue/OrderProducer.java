package com.ch.order.queue;

import com.ch.order.dto.order.OrderDto;
import com.ch.order.dto.queue.Field;
import com.ch.order.dto.queue.KafkaDto;
import com.ch.order.dto.queue.Payload;
import com.ch.order.dto.queue.Schema;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final String STRING = "string";
    private static final String INT32 = "int32";

    List<Field> fields = Arrays.asList(new Field(INT32, true, "order_id"),
            new Field(STRING, true, "user_id"),
            new Field(STRING, true, "product_id"),
            new Field(INT32, true, "qty"),
            new Field(INT32, true, "unit_price"),
            new Field(INT32, true, "total_price"));

    Schema schema = Schema.builder()
            .type("struct")
            .fields(fields)
            .optional(false)
            .name("orders")
            .build();

    // Order Message Producer
    public void sendOrder(String topic, OrderDto orderDto) {

        Payload payload = Payload.builder()
                .order_id(orderDto.getOrderId())
                .user_id(orderDto.getUserId())
                .product_id(orderDto.getProductId())
                .qty(orderDto.getQty())
                .unit_price(orderDto.getUnitPrice())
                .total_price(orderDto.getTotalPrice())
                .build();

        KafkaDto kafkaDto = KafkaDto.builder()
                .schema(schema)
                .payload(payload)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonInString = "";
        try {
            jsonInString = objectMapper.writeValueAsString(kafkaDto);
        } catch ( JsonProcessingException ex ) {
            ex.printStackTrace();
        }

        kafkaTemplate.send(topic, jsonInString);
        log.info("Order Message Send Success -> " + kafkaDto);
    }
}
