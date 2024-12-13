package com.finexchange.finexchange.dto.response;

import com.finexchange.finexchange.dto.CustomerDto;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthResponse {
    String message;
    String userId;
    String jwtToken;
    String refreshToken;
    boolean admin;
    List<CustomerDto> customers;

}
