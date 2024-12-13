package com.finexchange.finexchange.exception;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException() {
        super("Emir bulunamadı");
    }
}
