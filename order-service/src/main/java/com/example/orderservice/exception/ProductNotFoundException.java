package com.example.orderservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


public class ProductNotFoundException extends RuntimeException{
    public ProductNotFoundException(Long id) {
        super("Product Id " + id + " not found.");
    }
}