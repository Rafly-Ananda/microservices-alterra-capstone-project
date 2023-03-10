package com.example.orderservice.service;

import com.example.orderservice.dto.*;
import com.example.orderservice.entity.OrderDetailEntity;
import com.example.orderservice.entity.OrderEntity;
import com.example.orderservice.repository.OrderDetailRepository;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.repository.StatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderDetailRepository orderDetailRepository;
    @Mock
    private StatusRepository statusRepository;
    @InjectMocks
    private OrderService orderService;

    @Mock
    private RestTemplate restTemplate;
    @BeforeEach
    void setup (){

    }
    @Test
    void testGetAll() {
        // Setup
        List<OrderEntity> orders = new ArrayList<>();
        OrderEntity order1 = new OrderEntity(1L, 1, null, null, 100.0, LocalDateTime.now(), LocalDateTime.now());
        OrderEntity order2 = new OrderEntity(2L, 2, null, null, 200.0, LocalDateTime.now(), LocalDateTime.now());
        orders.add(order1);
        orders.add(order2);

        when(orderRepository.findAll()).thenReturn(orders);

        // Execute
        ResponseEntity<GlobalResponse> responseEntity = orderService.getAll();

        // Verify
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        GlobalResponse globalResponse = responseEntity.getBody();
        assertEquals("Orders found.", globalResponse.getMessage());
        assertEquals(2, ((List<OrderEntity>) globalResponse.getData()).size());
        assertEquals(order1.getUserId(), ((List<OrderEntity>) globalResponse.getData()).get(0).getUserId());
        assertEquals(order2.getUserId(), ((List<OrderEntity>) globalResponse.getData()).get(1).getUserId());
        assertEquals(order1.getTotal(), ((List<OrderEntity>) globalResponse.getData()).get(0).getTotal());
        assertEquals(order2.getTotal(), ((List<OrderEntity>) globalResponse.getData()).get(1).getTotal());
    }


}
