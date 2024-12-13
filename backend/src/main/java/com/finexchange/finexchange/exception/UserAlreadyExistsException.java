package com.finexchange.finexchange.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException() {
        super("User Already Exist!");
    }
}
