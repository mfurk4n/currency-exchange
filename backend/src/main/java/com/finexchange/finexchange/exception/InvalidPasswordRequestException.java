package com.finexchange.finexchange.exception;

public class InvalidPasswordRequestException extends RuntimeException {
    public InvalidPasswordRequestException() {
        super("Password or current password cannot be empty.");
    }
}
