package com.example.orderservice.exception;

public class OrderCancellationNotAllowedException extends RuntimeException{
    public OrderCancellationNotAllowedException(Long id) {
        super("Order Id " + id + " Cancellation not allowed");
    }
}