package com.finexchange.finexchange.exception;

public class UserInvalidAuthorizationException extends RuntimeException {
    public UserInvalidAuthorizationException() {
        super("User don't have the necessary authority");
    }
}
