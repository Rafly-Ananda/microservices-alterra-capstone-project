package com.example.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

    private Long product_id;

    //connect to category-service through productService
    private Long category_id;

    private String name;
    private String description;

    private Double price;

    private Integer stock;
}
