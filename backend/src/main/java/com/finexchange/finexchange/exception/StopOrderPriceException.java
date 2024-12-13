package com.finexchange.finexchange.exception;

public class StopOrderPriceException extends RuntimeException {
    public StopOrderPriceException() {
        super("Stop emir beklenen fiyat piyasa fiyatından yüksek veya eşit olamaz");
    }
}
