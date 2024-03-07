package com.ch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ChOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChOrderApplication.class, args);
    }
}
