package com.alterra.userservice.exceptions;

public class EmailAlreadyUsedException extends RuntimeException {

    public EmailAlreadyUsedException(String email) {
        super("Email " + email + " Already Used.");
    }
}
