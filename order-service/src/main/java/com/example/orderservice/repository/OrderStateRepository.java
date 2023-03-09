package com.example.orderservice.repository;

import com.example.orderservice.entity.OrderDetailEntity;
import com.example.orderservice.entity.OrderStateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderStateRepository extends JpaRepository<OrderStateEntity, Integer> {
}
