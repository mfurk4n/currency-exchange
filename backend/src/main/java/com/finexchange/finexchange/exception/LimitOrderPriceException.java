package com.finexchange.finexchange.exception;

public class LimitOrderPriceException extends RuntimeException {
    public LimitOrderPriceException() {
        super("Limit emir beklenen fiyat piyasa fiyatından yüksek veya eşit olamaz");
    }
}
