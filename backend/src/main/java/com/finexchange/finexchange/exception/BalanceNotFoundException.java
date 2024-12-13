package com.finexchange.finexchange.exception;

public class BalanceNotFoundException extends RuntimeException {
    public BalanceNotFoundException() {
        super("Cüzdana bağlı bakiye bulunamadı");
    }
}
