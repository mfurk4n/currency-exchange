package com.finexchange.finexchange.dto.response;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AvailableCurrencyResponse {
    String currencyId;
    String currencyName;

}