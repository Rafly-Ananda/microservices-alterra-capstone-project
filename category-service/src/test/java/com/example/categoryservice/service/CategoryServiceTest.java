package com.example.categoryservice.service;

import com.example.categoryservice.dto.GlobalResponse;
import com.example.categoryservice.entity.CategoryEntity;
import com.example.categoryservice.repository.CategoryRepository;
import com.example.categoryservice.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    public void testGetAll() {
        // Create mock data
        List<CategoryEntity> categories = new ArrayList<>();
        CategoryEntity order1 = new CategoryEntity( 1L, "Category 1", LocalDateTime.now(),LocalDateTime.now());
        CategoryEntity order2 = new CategoryEntity( 2L, "Category 2", LocalDateTime.now(),LocalDateTime.now());
        categories.add(order1);
        categories.add(order2);

        // Set up mock behavior for categoryRepository
        when(categoryRepository.findAll()).thenReturn(categories);

        // Call the getAll method
        ResponseEntity<GlobalResponse> responseEntity = categoryService.getAll();

        // Assert that the response status code is 200
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Assert that the response message is "Categories found."
        assertEquals("Categories found.", responseEntity.getBody().getMessage());

        // Assert that the response data contains the mock categories
        assertEquals(categories, responseEntity.getBody().getData());
    }

    @Test
    public void testGetById() {
        // Create a mock CategoryEntity object
        CategoryEntity category = new CategoryEntity( 1L, "Category 1", LocalDateTime.now(),LocalDateTime.now());

        // Set up the mock behavior for the categoryRepository
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        // Call the getById method with the mock ID
        ResponseEntity<GlobalResponse> responseEntity = categoryService.getById(1L);

        // Assert that the response status code is 200
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Assert that the response message is "Category Found."
        assertEquals("Category Found.", responseEntity.getBody().getMessage());

        // Assert that the response data contains the mock CategoryEntity object
        assertEquals(List.of(category), responseEntity.getBody().getData());
    }

    @Test
    public void testCreate() {
        // Create a mock CategoryEntity object
        CategoryEntity category = new CategoryEntity( 1L, "Category 1", LocalDateTime.now(),LocalDateTime.now());

        // Call the create method with the mock CategoryEntity object
        ResponseEntity<GlobalResponse> responseEntity = categoryService.create(category);

        // Assert that the response status code is 201
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

        // Assert that the response message is "Category Created."
        assertEquals("Category Created.", responseEntity.getBody().getMessage());

        // Assert that the response data contains the mock CategoryEntity object
        assertEquals(List.of(category), responseEntity.getBody().getData());
    }

    @Test
    public void testUpdate() {
        // Create a mock CategoryEntity object
        CategoryEntity category = new CategoryEntity( 1L, "Category 1", null,null);

        // Set up the mock behavior for the categoryRepository
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        // Call the update method with the mock CategoryEntity object and ID
        ResponseEntity<GlobalResponse> responseEntity = categoryService.update(new CategoryEntity( 1L, "Category 1", null,null), 1L);

        // Assert that the response status code is 200
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Assert that the response message is "Category 1 Updated."
        assertEquals("Category 1 Updated.", responseEntity.getBody().getMessage());

        // Assert that the response data contains the updated mock CategoryEntity object
        assertEquals(List.of(new CategoryEntity( 1L, "Category 1", null,null)), responseEntity.getBody().getData());
    }
}
