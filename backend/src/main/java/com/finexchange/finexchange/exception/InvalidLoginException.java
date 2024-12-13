package com.finexchange.finexchange.exception;

public class InvalidLoginException extends RuntimeException {
    public InvalidLoginException() {
        super("Geçersiz email veya şifre");
    }
}
