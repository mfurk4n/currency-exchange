package com.finexchange.finexchange.exception;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException() {
        super("Müşteri bulunamadı");
    }
}
