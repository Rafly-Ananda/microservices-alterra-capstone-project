package com.example.productservice.controller;


import com.example.productservice.dto.GlobalResponse;
import com.example.productservice.dto.ProductRequestDTO;
import com.example.productservice.dto.ProductWithCategoryDTO;
import com.example.productservice.entity.ProductEntity;
import com.example.productservice.service.AwsS3BucketStorageService;
import com.example.productservice.service.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import netscape.javascript.JSObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/api/v1/products")
@AllArgsConstructor
public class ProductController {
    private ProductService productService;
    private AwsS3BucketStorageService awsS3BucketStorageService;

    @GetMapping
    public ResponseEntity<GlobalResponse<ProductEntity>> getAll() {
        List<ProductEntity> productsList = productService.getAll();
        return new ResponseEntity<>(GlobalResponse.<ProductEntity>builder()
                .timestamp(LocalDateTime.now())
                .message("Products found.")
                .status(200)
                .data(productsList)
                .build(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GlobalResponse<ProductWithCategoryDTO>> getById(@PathVariable Long id){
        List<ProductWithCategoryDTO> productsList =  productService.getById(id);
        return new ResponseEntity<>(GlobalResponse.<ProductWithCategoryDTO>builder()
                .timestamp(LocalDateTime.now())
                .message("Product found.")
                .status(200)
                .data(productsList)
                .build(), HttpStatus.OK);

    }

    @PostMapping
    @SneakyThrows
    public ResponseEntity<GlobalResponse<ProductEntity>> create(@RequestPart("product") String product,
                                    @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        List<String> s3Keys = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        if (images != null) {
            log.info("saving to s3");
            List<String> uploadResponse = awsS3BucketStorageService.uploadFile(images);
            s3Keys.addAll(uploadResponse);
        }

        ProductRequestDTO newProductRequest = objectMapper.readValue(product, ProductRequestDTO.class);
        newProductRequest.setImages(s3Keys);

        ProductEntity savedProduct = productService.create(newProductRequest);

        return new ResponseEntity<>(GlobalResponse.<ProductEntity>builder()
                .timestamp(LocalDateTime.now())
                .message("Product Created.")
                .status(200)
                .data(List.of(savedProduct))
                .build(), HttpStatus.CREATED);
    }

    @PutMapping("/image/{id}")
    @SneakyThrows
    public ResponseEntity<GlobalResponse<ProductEntity>> updateWithImage(@RequestPart("product") String product,
                                                                @RequestPart(value = "images", required = false) List<MultipartFile> images,
                                                                @PathVariable Long id){
        List<String> s3Keys = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        if (images != null) {
            log.info("saving to s3");
            List<String> uploadResponse = awsS3BucketStorageService.uploadFile(images);
            s3Keys.addAll(uploadResponse);
        }

        ProductRequestDTO newProductRequest = objectMapper.readValue(product, ProductRequestDTO.class);
        s3Keys.addAll(newProductRequest.getImages());
        newProductRequest.setImages(s3Keys);

        ProductEntity updatedProduct = productService.update(newProductRequest, id);
        return new ResponseEntity<>(GlobalResponse.<ProductEntity>builder()
                .timestamp(LocalDateTime.now())
                .message("Product " + id + " Updated.")
                .status(HttpStatus.OK.value())
                .data(List.of(updatedProduct))
                .build(), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GlobalResponse<ProductEntity>> update(@RequestBody ProductRequestDTO product,@PathVariable Long id){
        ProductEntity updatedProduct = productService.update(product,id);
        return new ResponseEntity<>(GlobalResponse.<ProductEntity>builder()
                .timestamp(LocalDateTime.now())
                .message("Product " + id + " Updated.")
                .status(HttpStatus.OK.value())
                .data(List.of(updatedProduct))
                .build(), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GlobalResponse<ProductEntity>> delete(@PathVariable Long id){
        ProductEntity deletedProduct = productService.delete(id);
        return new ResponseEntity<>(GlobalResponse.<ProductEntity>builder()
                .timestamp(LocalDateTime.now())
                .message("Product Id " + id + " Is Deleted.")
                .status(200)
                .data(List.of(deletedProduct))
                .build(), HttpStatus.OK);
    }
}
