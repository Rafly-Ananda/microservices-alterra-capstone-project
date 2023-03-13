package com.example.orderservice.exception;

public class OrderStateNotFoundException extends RuntimeException{
    public OrderStateNotFoundException(Integer id)  {
        super("Order state Id " + id + " not found.");
    }
}
