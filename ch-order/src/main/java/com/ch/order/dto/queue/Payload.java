package com.ch.order.dto.queue;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Payload {

    private Long order_id;
    private String user_id;
    private String product_id;
    private int qty;
    private int unit_price;
    private int total_price;

}
