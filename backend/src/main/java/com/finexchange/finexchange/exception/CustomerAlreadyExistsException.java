package com.finexchange.finexchange.exception;

public class CustomerAlreadyExistsException extends RuntimeException{
    public CustomerAlreadyExistsException() {
        super("Bu müşteri mevcut!");
    }
}
