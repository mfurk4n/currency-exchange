package com.finexchange.finexchange.exception;

public class WalletNotFoundException extends RuntimeException {
    public WalletNotFoundException() {
        super("Cüzdan bulunamadı");
    }
    public WalletNotFoundException(String msg) {
        super(msg + " cüzdanı bulunamadı. İşleme devam edebilmek için cüzdan oluşturunuz.");
    }
}
