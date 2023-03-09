package com.example.orderservice.dto;

import lombok.Data;

@Data
public class OrderDetailDTO{
    private Long product_id;
    private Integer quantity;
    private Double sale_price;
}
