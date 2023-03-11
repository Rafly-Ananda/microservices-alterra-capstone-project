package com.example.productservice.controller;


import com.example.productservice.dto.GlobalResponse;
import com.example.productservice.dto.ProductWithCategoryDTO;
import com.example.productservice.entity.ProductEntity;
import com.example.productservice.service.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
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
    public ResponseEntity<GlobalResponse> getAll() {
        return productService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GlobalResponse> getById(@PathVariable Long id){
        return productService.getById(id);
    }

    @PostMapping
    public ResponseEntity<GlobalResponse> create(@RequestBody ProductEntity product) {
        return productService.create(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GlobalResponse> update(@RequestBody ProductEntity product,@PathVariable Long id){
        return productService.update(product,id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GlobalResponse> delete(@PathVariable Long id){
        return productService.delete(id);
    }
}
