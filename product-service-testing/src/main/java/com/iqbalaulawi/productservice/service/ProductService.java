package com.iqbalaulawi.productservice.service;

import com.iqbalaulawi.productservice.entity.ProductEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    List<ProductEntity> getAll();
    Optional<ProductEntity> getById(Long id);
    ProductEntity create(ProductEntity productEntity);
    ProductEntity update(Long id, ProductEntity productEntity);
    void delete(Long id);
}
