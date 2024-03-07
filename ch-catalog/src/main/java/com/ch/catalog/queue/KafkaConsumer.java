package com.ch.catalog.queue;

import com.ch.catalog.domain.Catalog;
import com.ch.catalog.repository.CatalogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final CatalogRepository catalogRepository;

    // Update Catalog Quantity
    @KafkaListener(topics = "ch-catalog-topic")
    public void updateQuantity(String kafkaMessage) {
        // Log (Info)
        log.info("Kafka Message -> " + kafkaMessage);

        Map<String, Object> map = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            map = objectMapper.readValue(kafkaMessage, new TypeReference<>(){});
        } catch ( JsonProcessingException ex ) {
            ex.printStackTrace();
        }

        Catalog catalogFromProductId = catalogRepository.findByProductId((String) map.get("productId"));
        if ( catalogFromProductId != null ) {
            catalogFromProductId.setStock(catalogFromProductId.getStock() - ((Integer) map.get("qty")));

            catalogRepository.save(catalogFromProductId);
        }

    }

}
