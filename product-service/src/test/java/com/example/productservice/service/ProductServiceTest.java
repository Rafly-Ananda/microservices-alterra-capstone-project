package com.example.productservice.service;


import com.example.productservice.dto.Category;
import com.example.productservice.dto.GlobalResponse;
import com.example.productservice.dto.ProductRequestDTO;
import com.example.productservice.dto.ProductWithCategoryDTO;
import com.example.productservice.entity.ProductEntity;
import com.example.productservice.exception.ProductNotFoundException;
import com.example.productservice.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private ProductService productService;
    @Mock
    private RestTemplate restTemplate;
    public void setUp() {

    }
    @Test
    public void getAllTest() {
        // Create a mock list of products
        List<ProductEntity> products = new ArrayList<>();
        products.add(new ProductEntity(1L, 1L, "Product 1", "Description 1", 10.0, 100, new ArrayList<>(), null, null));
        products.add(new ProductEntity(2L, 2L, "Product 2", "Description 2", 20.0, 200, new ArrayList<>(), null, null));

        // Configure mock behavior
        when(productRepository.findAll()).thenReturn(products);

        // Call the method to test
        List<ProductEntity> result = productService.getAll();

        // Verify mock interactions
        verify(productRepository, times(1)).findAll();

        // Check the result
        assertEquals(products, result);
    }
    @Test
    public void testGetById() throws Exception {

    }
    @Test
    public void testGetAllByUserId() {

    }
    @Test
    public void testCreate() {

        // Create a mock ProductRequestDTO object
        ProductRequestDTO productRequestDTO = new ProductRequestDTO();
        productRequestDTO.setCategory_id(1L);
        productRequestDTO.setName("Test Product");
        productRequestDTO.setDescription("This is a test product.");
        productRequestDTO.setPrice(9.99);
        productRequestDTO.setStock(10);
        productRequestDTO.setImages(Collections.singletonList("test_image.png"));

        // Create a mock ProductEntity object to return from the repository
        ProductEntity savedProduct = new ProductEntity();
        savedProduct.setProduct_id(1L);
        savedProduct.setCategory_id(productRequestDTO.getCategory_id());
        savedProduct.setName(productRequestDTO.getName());
        savedProduct.setDescription(productRequestDTO.getDescription());
        savedProduct.setPrice(productRequestDTO.getPrice());
        savedProduct.setStock(productRequestDTO.getStock());
        savedProduct.setImages(productRequestDTO.getImages());

        when(productRepository.save(any(ProductEntity.class))).thenReturn(savedProduct);

        ProductEntity newProduct = productService.create(productRequestDTO);

        Assertions.assertEquals(productRequestDTO.getCategory_id(), newProduct.getCategory_id());
        Assertions.assertEquals(productRequestDTO.getName(), newProduct.getName());
        Assertions.assertEquals(productRequestDTO.getDescription(), newProduct.getDescription());
        Assertions.assertEquals(productRequestDTO.getPrice(), newProduct.getPrice());
        Assertions.assertEquals(productRequestDTO.getStock(), newProduct.getStock());
        Assertions.assertEquals(productRequestDTO.getImages(), newProduct.getImages());
    }

    @Test
    public void testUpdate() {

    }

    @Test
    public void testDelete() {
        Long productId = 1L;
        ProductEntity product = new ProductEntity();
        product.setProduct_id(productId);
        product.setName("Test Product");
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        ProductEntity deletedProduct = productService.delete(productId);

        verify(productRepository, times(1)).deleteById(productId);
        assertEquals(productId, deletedProduct.getProduct_id());
        assertEquals("Test Product", deletedProduct.getName());
    }

    @Test
    public void testDeleteNotFound() {
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> {
            productService.delete(productId);
        });

        verify(productRepository, never()).deleteById(productId);
    }









}
