package com.example.productservice.dto;

import com.example.productservice.entity.ProductEntity;
import lombok.Data;

@Data
public class ProductWithCategoryDTO {
    ProductEntity product;
    CategoryDTO categoryDTO;
}
