package com.example.orderservice.controller;

import com.example.orderservice.dto.CreateOrderRequestDTO;
import com.example.orderservice.dto.GlobalResponse;
import com.example.orderservice.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@AllArgsConstructor
public class OrderController {
    private OrderService orderService;
    @GetMapping
    public ResponseEntity<GlobalResponse> getOrder() {
        return orderService.getAll();
    }
//    @GetMapping("/user/{id}")
//    public ResponseEntity<GlobalResponse> getOrderByUserId(@PathVariable Long user_id) {
//        return orderService.getAllByUserId(user_id);
//    }
    @GetMapping("/{id}")
    public ResponseEntity<GlobalResponse> getOrderById(@PathVariable Long id) {
        return orderService.getById(id);
    }
    @PostMapping
    public ResponseEntity<GlobalResponse> createOrder(@RequestBody CreateOrderRequestDTO request) {
        return orderService.create(request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GlobalResponse> deleteOrder(@PathVariable Long id) {
        return orderService.delete(id);
    }
}
