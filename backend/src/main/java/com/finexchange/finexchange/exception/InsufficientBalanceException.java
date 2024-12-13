package com.finexchange.finexchange.exception;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException() {
        super("Bakiyeniz yetersiz");
    }
}
