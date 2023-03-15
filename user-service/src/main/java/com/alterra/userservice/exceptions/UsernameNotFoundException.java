package com.alterra.userservice.exceptions;

public class UsernameNotFoundException extends RuntimeException{
    public UsernameNotFoundException(String username) {
        super("User :" + username + " Does Not Exist.");
    }
}
