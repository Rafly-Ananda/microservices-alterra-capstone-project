package com.example.orderservice.dto;

import com.example.orderservice.entity.OrderDetailEntity;
import com.example.orderservice.entity.OrderEntity;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequestDTO {
    private OrderDTO Order;
    private List<OrderDetailDTO> orderDetail;
}




