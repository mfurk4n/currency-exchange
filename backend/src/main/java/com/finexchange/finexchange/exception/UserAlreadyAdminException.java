package com.finexchange.finexchange.exception;

public class UserAlreadyAdminException extends RuntimeException {
    public UserAlreadyAdminException() {
        super("Kullanıcı admin yetkisine sahip!");
    }
}
