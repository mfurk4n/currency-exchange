package com.finexchange.finexchange.exception;

public class WalletAlreadyExistException extends RuntimeException {
    public WalletAlreadyExistException() {
        super("Bu kura ait cüzdan zaten mevcut");
    }
}
