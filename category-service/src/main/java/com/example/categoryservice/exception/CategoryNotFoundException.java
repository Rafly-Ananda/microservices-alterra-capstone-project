package com.example.categoryservice.exception;

public class CategoryNotFoundException extends RuntimeException{
    public CategoryNotFoundException(Long id) {
        super("Category Id " + id + " not found.");
    }
}
