package com.ch.catalog.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class CommonApi {

    @GetMapping("/healthCheck")
    public String status() {
        return "Working In Catalog Service.";
    }
}
