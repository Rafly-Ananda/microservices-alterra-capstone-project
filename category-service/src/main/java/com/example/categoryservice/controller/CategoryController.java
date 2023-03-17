package com.example.categoryservice.controller;

import com.example.categoryservice.dto.GlobalResponse;
import com.example.categoryservice.entity.CategoryEntity;
import com.example.categoryservice.service.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/categories")
@AllArgsConstructor
public class CategoryController {
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<GlobalResponse> getAll() {
        return categoryService.getAll();
    }
    @GetMapping("/{id}")
    public ResponseEntity<GlobalResponse> getById(@PathVariable Long id){
        return categoryService.getById(id);
    }

    @PostMapping
    public ResponseEntity<GlobalResponse> create(@RequestBody CategoryEntity category) {
        return categoryService.create(category);
    }
    @PutMapping("/{id}")
    public ResponseEntity<GlobalResponse> update(@RequestBody CategoryEntity category,@PathVariable Long id){
        return categoryService.update(category,id);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<GlobalResponse> delete(@PathVariable Long id){
        return categoryService.delete(id);
    }

}
