package com.finexchange.finexchange.dto.response;

import com.finexchange.finexchange.dto.CustomerDto;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerSelectResponse {
    String jwtToken;
    CustomerDto customer;
}
