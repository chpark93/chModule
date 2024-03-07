package com.ch.order.queue;

import com.ch.order.dto.order.OrderDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    // Kafka Producer Send - Order
    public void sendOrder(String topic, OrderDto orderDto) {
        ObjectMapper objectMapper = new ObjectMapper();

        String jsonInString = "";
        try {
            jsonInString = objectMapper.writeValueAsString(orderDto);
        } catch ( JsonProcessingException ex ) {
            ex.printStackTrace();
        }

        kafkaTemplate.send(topic, jsonInString);
        log.info("Producer Send Data Success - Order -> " + orderDto);
    }
}
