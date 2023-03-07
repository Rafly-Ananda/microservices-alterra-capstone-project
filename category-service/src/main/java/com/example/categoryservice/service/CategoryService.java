package com.example.categoryservice.service;

import com.example.categoryservice.dto.GlobalResponse;
import com.example.categoryservice.entity.CategoryEntity;
import com.example.categoryservice.exception.CategoryNotFoundException;
import com.example.categoryservice.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;


    public ResponseEntity<GlobalResponse> getAll() {
        List<CategoryEntity> categories = categoryRepository.findAll();
        log.info("Get all Categories :");
        return new ResponseEntity<>(GlobalResponse.builder()
                .timestamp(LocalDateTime.now())
                .message("Categories found.")
                .status(200)
                .data(categories)
                .build(), HttpStatus.OK);
    }

    public ResponseEntity<GlobalResponse> getById(Long id) {
        CategoryEntity category = categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException(id));
        return new ResponseEntity<>(GlobalResponse.builder()
                .timestamp(LocalDateTime.now())
                .message("Category Found.")
                .status(200)
                .data(List.of(category))
                .build(), HttpStatus.OK);
    }

    public ResponseEntity<GlobalResponse> create(CategoryEntity category) {
        categoryRepository.save(category);

        return new ResponseEntity<>(GlobalResponse.builder()
                .timestamp(LocalDateTime.now())
                .message("Category Created.")
                .status(200)
                .data(List.of(category))
                .build(), HttpStatus.CREATED);
    }
    public ResponseEntity<GlobalResponse> update(CategoryEntity categoryEntity, Long id) {
        CategoryEntity category = categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException(id));
        category.setName(categoryEntity.getName());

        categoryRepository.save(category);
        return new ResponseEntity<>(GlobalResponse.builder()
                .timestamp(LocalDateTime.now())
                .message("Category " + id + " Updated.")
                .status(200)
                .data(List.of(categoryEntity))
                .build(), HttpStatus.OK);
    }

    public ResponseEntity<GlobalResponse> delete(Long id) {
        CategoryEntity product = categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException((id)));
        categoryRepository.deleteById(id);
        return new ResponseEntity<>(GlobalResponse.builder()
                .timestamp(LocalDateTime.now())
                .message("Category Id " + id + " Is Deleted.")
                .status(200)
                .data(List.of(product))
                .build(), HttpStatus.OK);
    }
}
