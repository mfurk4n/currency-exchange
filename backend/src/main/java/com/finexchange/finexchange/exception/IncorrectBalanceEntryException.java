package com.finexchange.finexchange.exception;

public class IncorrectBalanceEntryException extends RuntimeException {
    public IncorrectBalanceEntryException() {
        super("Bakiye değeri boş veya 0'dan küçük olamaz.");
    }
}
