package com.example.productservice.service;

import com.example.productservice.dto.CategoryDTO;
import com.example.productservice.dto.ProductWithCategoryDTO;
import com.example.productservice.entity.ProductEntity;
import com.example.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final RestTemplate restTemplate;
    public List<ProductEntity> getAll() {
        log.info("Get all product");

        List<ProductEntity> products = productRepository.findAll();

        return products;
    }

    public ProductWithCategoryDTO getById (Long product_id){
        ProductEntity product = productRepository.findByProductId(product_id);
        CategoryDTO categoryDTO =
                restTemplate.getForObject("http://category-service:8083/api/v1/categories/" + product.getCategory_id(),
                        CategoryDTO.class);
        ProductWithCategoryDTO productWithCategoryDTO = new ProductWithCategoryDTO();
        productWithCategoryDTO.setProduct(product);
        productWithCategoryDTO.setCategoryDTO(categoryDTO);
        log.info("Get all product");
        return productWithCategoryDTO;
    }
    public ProductEntity createProduct(ProductEntity product) {
        log.info("Success add product : {}", product.toString());
        return productRepository.save(product);
    }
    public void deleteById(Long product_id) {
        productRepository.deleteById(product_id);
    }

}
