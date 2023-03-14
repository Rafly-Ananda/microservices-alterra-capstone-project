package com.example.orderservice.repository;

import com.example.orderservice.entity.OrderEntity;
import jakarta.persistence.criteria.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findByUserId(Integer userId);

    OrderEntity findByOrderId(Long id);
}
