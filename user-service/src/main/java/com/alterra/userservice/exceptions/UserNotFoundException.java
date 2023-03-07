package com.alterra.userservice.exceptions;

public class UserNotFoundException extends RuntimeException{

    public UserNotFoundException(Integer id) {
        super("User With Id " + id + " Does Not Exist.");
    }
}