package com.ch.order.api;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CommonApi {

    @GetMapping("/healthCheck")
    @Timed(value = "orders.status", longTask = true)
    public String status() {
        return "Working In Order Service";
    }
}
