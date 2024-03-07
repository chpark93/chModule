package com.ch.catalog.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CatalogResponse {

    private String productId;
    private String productName;
    private Integer unitPrice;
    private Integer stock;

    private LocalDateTime createdAt;
}
