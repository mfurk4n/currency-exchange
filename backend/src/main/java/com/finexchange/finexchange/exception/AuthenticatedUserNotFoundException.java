package com.finexchange.finexchange.exception;

public class AuthenticatedUserNotFoundException extends RuntimeException {
    public AuthenticatedUserNotFoundException() {
        super("Session Not Found");
    }
}
