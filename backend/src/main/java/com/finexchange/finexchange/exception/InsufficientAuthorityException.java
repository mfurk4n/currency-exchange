package com.finexchange.finexchange.exception;

public class InsufficientAuthorityException extends RuntimeException {
    public InsufficientAuthorityException() {
        super("Bu işlem için yetkiniz bulunmamaktadır.");
    }
}
