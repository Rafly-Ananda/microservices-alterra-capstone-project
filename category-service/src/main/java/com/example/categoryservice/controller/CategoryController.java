package com.example.categoryservice.controller;

import com.example.categoryservice.dto.ResponseDTO;
import com.example.categoryservice.entity.CategoryEntity;
import com.example.categoryservice.service.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/categories")
@AllArgsConstructor
public class CategoryController {
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ResponseDTO> getAll(){
        List<CategoryEntity> products = categoryService.getAll();
        if (products.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            ResponseDTO responseDTO = new ResponseDTO(
                    "success",
                    "all products",
                    products
            );
            return ResponseEntity.ok(responseDTO);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO> getById(@PathVariable Long id){
        Optional<CategoryEntity> category = categoryService.getById(id);
        if (category.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            ResponseDTO responseDTO = new ResponseDTO(
                    "success",
                    "detail category id:"+id,
                    category
            );
            return ResponseEntity.ok(responseDTO);
        }
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> create(@RequestBody CategoryEntity category){
        CategoryEntity saved_category =  categoryService.create(category);

        ResponseDTO responseDTO = new ResponseDTO(
                "success",
                "products created",
                saved_category
        );
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO> update(@PathVariable Long id,@RequestBody CategoryEntity category){
        CategoryEntity updated_category = categoryService.update(category,id);
        ResponseDTO responseDTO = new ResponseDTO(
                "success",
                "products updated id:"+id,
                updated_category
        );
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO> delete(@PathVariable Long id){
        categoryService.deleteById(id);
        ResponseDTO responseDTO = new ResponseDTO(
                "success",
                "products deleted",
                null
        );
        return  ResponseEntity.ok(responseDTO);
    }

}
