package com.finexchange.finexchange.exception;

public class UnauthorizedAccessException extends RuntimeException {
    public UnauthorizedAccessException() {
        super("You do not have permission to access this wallet.");
    }
}
