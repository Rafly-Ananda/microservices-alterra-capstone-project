package com.alterra.userservice.exceptions;

public class UsernameAlreadyUsedException extends RuntimeException {

    public UsernameAlreadyUsedException(String username) {
        super("Username " + username + " Already Used.");
    }
}
