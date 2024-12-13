package com.finexchange.finexchange.exception;

public class CurrencyNotFoundException extends RuntimeException{
    public CurrencyNotFoundException() {
        super("Currency not found!");
    }
}
