package com.example.orderservice.exception;

public class StockInsufficientException extends RuntimeException{
    public StockInsufficientException(Long id) {
        super("Order failed, Produk Id " + id + " : Insufficient Stock");
    }
}
