package com.ch.order.repository;

import com.ch.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Iterable<Order> findByUserId(String userId);
}
