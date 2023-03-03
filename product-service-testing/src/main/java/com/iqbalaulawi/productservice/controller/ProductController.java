package com.iqbalaulawi.productservice.controller;

import com.iqbalaulawi.productservice.entity.ProductEntity;
import com.iqbalaulawi.productservice.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@RequestMapping("/api/v1/products")
@AllArgsConstructor // biar @Autowired jgn lupa
public class ProductController {
    private ProductService productService;
    @GetMapping("/test")
    public String testing(){
        return "Connection to aws success ...";
    }
    @GetMapping
    public ResponseEntity<Object> getAll(){
        List<ProductEntity> products = productService.getAll();
        Map<String, Object> response = new HashMap<>();
        if (products.isEmpty()) {
            response.put("status", "404 Not Found");
            response.put("message", "Data Products tidak ditemukan");
            response.put("data", products);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        response.put("status", "200 Found");
        response.put("message", "Data Products");
        response.put("data", products);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{id}")
    public Optional<ProductEntity> getById(@PathVariable Long id){
        Optional<ProductEntity> productbyid = productService.getById(id);
        return productbyid;
    }
    @PostMapping
    public ProductEntity create(@RequestBody ProductEntity productEntity){
        return productService.create(productEntity);
    }
    @PutMapping("/{id}")
    public ProductEntity update(@PathVariable Long id,@RequestBody ProductEntity productEntity){
        return  productService.update(id,productEntity);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id){
        productService.delete(id);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Berhasil menghapus Product ["+id+"]");
        return ResponseEntity.ok(response);
    }
}
