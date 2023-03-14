package com.example.productservice.service;

import com.example.productservice.dto.Category;
import com.example.productservice.dto.GlobalResponse;
import com.example.productservice.dto.ProductRequestDTO;
import com.example.productservice.dto.ProductWithCategoryDTO;
import com.example.productservice.entity.ProductEntity;
import com.example.productservice.exception.CategoryNotFoundException;
import com.example.productservice.exception.ProductNotFoundException;
import com.example.productservice.repository.ProductRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
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

//    private final String categoryServiceUrl = "http://localhost:8083/api/v1/categories/";
    private final String categoryServiceUrl = "http://category-service:8083/api/v1/categories/";

    public List<ProductEntity> getAll() {
        List<ProductEntity> products = productRepository.findAll();
        log.info("Get all product");
        return products;
    }

    public List<ProductWithCategoryDTO> getById (Long id) {
        Optional<ProductEntity> product = productRepository.findById(id);

        if (product.isPresent()) {
            RestTemplate restTemplate = new RestTemplate();
            Category categoryDTO = new Category();

            try{
                String categoryUrl = categoryServiceUrl + product.get().getCategory_id();
                ResponseEntity<String> response = restTemplate.getForEntity(categoryUrl, String.class);
                log.info("calling : " + categoryServiceUrl +  product.get().getCategory_id());

                if(response.getStatusCode() == HttpStatus.OK){
                    log.info("Get product");
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.findAndRegisterModules();
                    GlobalResponse<Category> categoryResponse = mapper.readValue(response.getBody(), new TypeReference<GlobalResponse<Category>>() {});
                    categoryDTO.setP_category_id(categoryResponse.getData().get(0).getP_category_id());
                    categoryDTO.setName(categoryResponse.getData().get(0).getName());
                    categoryDTO.setCreateAt(categoryResponse.getData().get(0).getCreateAt());
                    categoryDTO.setUpdatedAt(categoryResponse.getData().get(0).getUpdatedAt());
                }

            }catch(Exception e){
                log.info("Category not found");
            }

            ProductWithCategoryDTO productWithCategoryDTO = new ProductWithCategoryDTO();
            productWithCategoryDTO.setProduct(product.get());
            productWithCategoryDTO.setCategory(categoryDTO);

            List<ProductWithCategoryDTO> productList = new ArrayList<>();
            productList.add(productWithCategoryDTO);

            return productList;

        }else{
            throw new ProductNotFoundException(id);
        }
    }

    public ProductEntity create(ProductRequestDTO product) {
        ProductEntity newProduct = new ProductEntity();
        newProduct.setCategory_id(product.getCategory_id());
        newProduct.setName(product.getName());
        newProduct.setDescription(product.getDescription());
        newProduct.setPrice(product.getPrice());
        newProduct.setStock(product.getStock());
        newProduct.setImages(product.getImages());

        return productRepository.save(newProduct);
    }

    public ProductEntity update(ProductEntity productEntity, Long id) {
        ProductEntity product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));

        RestTemplate restTemplate = new RestTemplate();

        try{
            log.info("calling : " + categoryServiceUrl +  productEntity.getCategory_id());
            String categoryUrl = categoryServiceUrl + productEntity.getCategory_id();
            ResponseEntity<String> response = restTemplate.getForEntity(categoryUrl, String.class);

            if(response.getStatusCode() == HttpStatus.OK){

                log.info("Category found");
                ObjectMapper mapper = new ObjectMapper();
                mapper.findAndRegisterModules();
                GlobalResponse<Category> categoryResponse = mapper.readValue(response.getBody(), new TypeReference<GlobalResponse<Category>>() {});
                Category categoryDTO = new Category();
                categoryDTO.setP_category_id(categoryResponse.getData().get(0).getP_category_id());
                categoryDTO.setName(categoryResponse.getData().get(0).getName());
                categoryDTO.setCreateAt(categoryResponse.getData().get(0).getCreateAt());
                categoryDTO.setUpdatedAt(categoryResponse.getData().get(0).getUpdatedAt());

                product.setCategory_id(productEntity.getCategory_id());
                product.setName(productEntity.getName());
                product.setPrice(productEntity.getPrice());
                product.setStock(productEntity.getStock());
                log.info("Updating Product");
                productRepository.save(product);
                return product;
            } else {
                throw new CategoryNotFoundException(productEntity.getCategory_id());
            }
        }catch(Exception e){
            log.info("Failed update product: "+e);
            throw new CategoryNotFoundException(productEntity.getCategory_id());
        }
    }

    public ProductEntity delete(Long id) {
        ProductEntity product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException((id)));
        productRepository.deleteById(id);
        return product;
    }

}
