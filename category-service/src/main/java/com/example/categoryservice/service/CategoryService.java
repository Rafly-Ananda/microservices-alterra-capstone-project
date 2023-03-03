package com.example.categoryservice.service;

import com.example.categoryservice.entity.CategoryEntity;
import com.example.categoryservice.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;


    public List<CategoryEntity> getAll() {
        return categoryRepository.findAll();
    }

    public Optional<CategoryEntity> getById(Long id) {
        return categoryRepository.findById(id);
    }

    public CategoryEntity create(CategoryEntity category) {
        return categoryRepository.save(category);
    }
    public CategoryEntity update(CategoryEntity product, Long id) {
        Optional<CategoryEntity> update_product = categoryRepository.findById(id);
        CategoryEntity update = update_product.get();
        update.setName(product.getName());
        return categoryRepository.save(update);
    }

    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }
}
