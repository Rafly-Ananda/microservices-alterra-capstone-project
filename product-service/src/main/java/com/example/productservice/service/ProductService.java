package com.example.productservice.service;

import com.example.categoryservice.entity.CategoryEntity;
import com.example.productservice.entity.ProductEntity;
import com.example.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private RestTemplate restTemplate;
    public List<ProductEntity> getAllProducts() {
        return productRepository.findAll();
    }
    public ProductEntity createProduct(ProductEntity product) {
        return productRepository.save(product);
    }
}
