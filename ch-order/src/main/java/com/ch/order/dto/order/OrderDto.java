package com.ch.order.dto.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDto {

    private String productId;
    private Integer qty;
    private Integer unitPrice;
    private Integer totalPrice;

    private Long orderId;
    private String userId;
}
