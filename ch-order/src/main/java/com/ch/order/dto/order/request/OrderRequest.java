package com.ch.order.dto.order.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderRequest {

    private String productId;
    private Integer qty;
    private Integer unitPrice;

}
