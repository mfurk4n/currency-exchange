package com.finexchange.finexchange.dto.response.chart;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ExchangeRatesMinimal {
    BigDecimal ask;
    BigDecimal bid;
    String dataDate;
}
