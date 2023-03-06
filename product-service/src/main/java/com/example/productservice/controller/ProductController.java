package com.example.productservice.controller;


import com.example.productservice.dto.ProductWithCategoryDTO;
import com.example.productservice.entity.ProductEntity;
import com.example.productservice.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@AllArgsConstructor
public class ProductController {
    private ProductService productService;
    @GetMapping
    public List<ProductEntity> getAll() {
        List<ProductEntity> products = productService.getAll();

        return products;
    }
    @GetMapping
    public ProductWithCategoryDTO getById(Long product_id){
        ProductWithCategoryDTO product = productService.getById(product_id);
        return product;
    }

    @PostMapping
    public ResponseEntity<ProductEntity> create(@RequestBody ProductEntity product) {
        ProductEntity createdProduct = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }
}
