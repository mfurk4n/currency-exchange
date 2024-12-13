package com.finexchange.finexchange.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateDto {
    String id;
    String exchangeCode;
    BigDecimal ask;
    BigDecimal bid;
    CurrencyDto baseCurrency;
    CurrencyDto targetCurrency;
    String dataDate;
    BigDecimal changeRate;
}
