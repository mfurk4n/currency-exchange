package com.finexchange.finexchange.exception;

public class InvalidCustomerNationalIdException extends RuntimeException {
    public InvalidCustomerNationalIdException() {
        super("Gerçek kişiler için geçerli bir TC Kimlik Numarası gereklidir.");
    }
}
