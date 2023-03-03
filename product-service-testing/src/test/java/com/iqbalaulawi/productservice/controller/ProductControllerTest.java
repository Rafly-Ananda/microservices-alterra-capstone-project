package com.iqbalaulawi.productservice.controller;

import com.iqbalaulawi.productservice.entity.ProductEntity;
import com.iqbalaulawi.productservice.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.Extensions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {
    private MockMvc mockMvc;

    @InjectMocks
    private ProductController productController;

    @Mock
    private ProductService productService;

    @BeforeEach
    public void setup() {
        mockMvc = standaloneSetup(productController).build();
    }
    @Test
    public void testgetAllIfProductsEmpty() throws Exception {
        //arrange
        List<ProductEntity> actual = new ArrayList<>();
        //act - invoke
        when(productService.getAll()).thenReturn(actual);
        //assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.content().json("{\r\n  \"data\": [],\r\n  \"message\": \"Data Products tidak ditemukan\",\r\n  \"status\": \"404 Not Found\"\r\n}"));
    }
    @Test
    public void testgetAll() throws Exception {
        List<ProductEntity> actual = List.of(new ProductEntity(1L,"nama","deskripsi",1,1.0));
        Map<String, Object> response = new HashMap<>();
        response.put("status", "200 Found");
        response.put("message", "Data Products");
        response.put("data", actual);


        when(productService.getAll()).thenReturn(actual);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("{\r\n  \"data\": [\r\n    {\r\n      \"id\": 1,\r\n      \"name\": \"nama\",\r\n      \"description\": \"deskripsi\",\r\n      \"stock\": 1,\r\n      \"price\": 1.0\r\n    }\r\n  ],\r\n  \"message\": \"Data Products\",\r\n  \"status\": \"200 Found\"\r\n}"));
    }

}
