package com.codeandlearn.OrderService.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codeandlearn.OrderService.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
