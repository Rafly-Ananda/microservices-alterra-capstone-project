package com.example.orderservice.controller;

import com.example.orderservice.dto.ChangStateOrderDTO;
import com.example.orderservice.dto.CreateOrderRequestDTO;
import com.example.orderservice.dto.GlobalResponse;
import com.example.orderservice.entity.OrderStateEntity;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.service.OrderStateService;
//import jakarta.persistence.Cacheable;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class OrderController {
    private OrderService orderService;
    private OrderStateService orderStateService;
    @GetMapping
    public ResponseEntity<GlobalResponse> getOrder() {
        return orderService.getAll();
    }
    @GetMapping("/{id}")
    public ResponseEntity<GlobalResponse> getOrderById(@PathVariable Long id) {
        return orderService.getById(id);
    }
    @GetMapping("/user/{user_id}")
    public ResponseEntity<GlobalResponse> getOrderByUserId(@PathVariable Integer user_id) {
        return orderService.getAllByUserId(user_id);
    }
    @PostMapping
    public ResponseEntity<GlobalResponse> createOrder(@RequestBody CreateOrderRequestDTO request) {
        return orderService.create(request);
    }
    @PutMapping("/update-state-order/{id}")
    public ResponseEntity<GlobalResponse> updateOrder(@PathVariable Long id, @RequestBody ChangStateOrderDTO stateOrderDTO) {
        return orderService.updateStateOrder(id,stateOrderDTO.getOrder_state());
    }
    @PutMapping("/cancel-state-order/{order_id}")
    public ResponseEntity<GlobalResponse> cancelOrder(@PathVariable Long order_id) {
        return orderService.cancelOrderByOrderId(order_id);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<GlobalResponse> deleteOrder(@PathVariable Long id) {
        return orderService.delete(id);
    }


    @GetMapping("/orderstates")
    public List<OrderStateEntity> getOrderState(){
        return orderStateService.getAll();
    }
    @PostMapping("/seed-orderstates")
    public List<OrderStateEntity> seedOrderState(){
        return orderStateService.seedOrderState();
    }

}
