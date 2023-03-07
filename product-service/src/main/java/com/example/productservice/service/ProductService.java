package com.example.productservice.service;

import com.example.productservice.dto.CategoryDTO;
import com.example.productservice.dto.GlobalResponse;
import com.example.productservice.dto.ProductWithCategoryDTO;
import com.example.productservice.entity.ProductEntity;
import com.example.productservice.exception.CategoryNotFoundException;
import com.example.productservice.exception.ProductNotFoundException;
import com.example.productservice.repository.ProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final RestTemplate restTemplate;
    public ResponseEntity<GlobalResponse> getAll() {
        List<ProductEntity> products = productRepository.findAll();
        log.info("Get all product");
        return new ResponseEntity<>(GlobalResponse.builder()
                .timestamp(LocalDateTime.now())
                .message("Products found.")
                .status(200)
                .data(products)
                .build(), HttpStatus.OK);
    }

    public ResponseEntity<GlobalResponse> getById (Long id) {
        Optional<ProductEntity> product = productRepository.findById(id);
        if (product.isPresent()) {
            RestTemplate restTemplate = new RestTemplate();
            CategoryDTO categoryDTO = new CategoryDTO();
            try{
                String categoryUrl = "http://category-service:8083/api/v1/categories/" + product.get().getCategory_id();
                ResponseEntity<String> response = restTemplate.getForEntity(categoryUrl, String.class);
                log.info("calling : http://category-service:8083/api/v1/categories/" + product.get().getCategory_id());
                if(response.getStatusCode() == HttpStatus.OK){
                    log.info("Get product");
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode root = mapper.readTree(response.getBody());
                    categoryDTO.setP_category_id((long) root.path("data").path(0).path("p_category_id").asInt());
                    categoryDTO.setName(root.path("data").path(0).path("name").asText());
                }
            }catch(Exception e){
                log.info("Category not found");
            }
            ProductWithCategoryDTO productWithCategoryDTO = new ProductWithCategoryDTO();
            productWithCategoryDTO.setProduct(product.get());
            productWithCategoryDTO.setCategoryDTO(categoryDTO);

            List<ProductWithCategoryDTO> productList = new ArrayList<>();
            productList.add(productWithCategoryDTO);

            return new ResponseEntity<>(GlobalResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .message("Product found.")
                    .status(200)
                    .data(productList)
                    .build(), HttpStatus.OK);
        }else{
            throw new ProductNotFoundException(id);
        }
    }
    public ResponseEntity<GlobalResponse> create(ProductEntity product) {
        productRepository.save(product);

        return new ResponseEntity<>(GlobalResponse.builder()
                .timestamp(LocalDateTime.now())
                .message("Product Created.")
                .status(200)
                .data(List.of(product))
                .build(), HttpStatus.CREATED);
    }
    public ResponseEntity<GlobalResponse> update(ProductEntity productEntity, Long id) {
        ProductEntity product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));

        RestTemplate restTemplate = new RestTemplate();

        try{
            log.info("calling : http://category-service:8083/api/v1/categories/" + productEntity.getCategory_id());

            String categoryUrl = "http://category-service:8083/api/v1/categories/" + productEntity.getCategory_id();
            ResponseEntity<String> response = restTemplate.getForEntity(categoryUrl, String.class);

            if(response.getStatusCode() == HttpStatus.OK){
                log.info("Category found");
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());
                CategoryDTO categoryDTO = new CategoryDTO();
                categoryDTO.setP_category_id((long) root.path("data").path(0).path("p_category_id").asInt());
                categoryDTO.setName(root.path("data").path(0).path("name").asText());
                product.setCategory_id(productEntity.getCategory_id());
                product.setName(productEntity.getName());
                product.setPrice(productEntity.getPrice());
                product.setStock(productEntity.getStock());
                log.info("Updating Product");
                productRepository.save(product);
                return new ResponseEntity<>(GlobalResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .message("Product " + id + " Updated.")
                        .status(HttpStatus.OK.value())
                        .data(List.of(product))
                        .build(), HttpStatus.OK);
            } else {
                throw new CategoryNotFoundException(productEntity.getCategory_id());
            }
        }catch(Exception e){
            log.info("Failed update product: "+e);
            throw new CategoryNotFoundException(productEntity.getCategory_id());
        }
    }
    public ResponseEntity<GlobalResponse> delete(Long id) {
        ProductEntity product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException((id)));
        productRepository.deleteById(id);
        return new ResponseEntity<>(GlobalResponse.builder()
                .timestamp(LocalDateTime.now())
                .message("Product Id " + id + " Is Deleted.")
                .status(200)
                .data(List.of(product))
                .build(), HttpStatus.OK);
    }



}
