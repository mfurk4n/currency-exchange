package com.finexchange.finexchange.exception;

public class InvalidCustomerTaxIdException extends RuntimeException {
    public InvalidCustomerTaxIdException() {
        super("Tüzel kişilikler için geçerli bir Vergi Kimlik Numarası gereklidir.");
    }
}
