package com.finexchange.finexchange.exception;

public class ExchangeRateNotFoundException extends RuntimeException {
    public ExchangeRateNotFoundException() {
        super("Döviz kuru oranları bulunamadı");
    }
}
