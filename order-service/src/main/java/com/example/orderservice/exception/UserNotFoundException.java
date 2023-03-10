package com.example.orderservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(Integer id) {
        super("User Id " + id + " not found.");
    }
}