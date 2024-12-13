package com.finexchange.finexchange.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    String id;
    CustomerDto customer;
    CurrencyDto baseCurrency;
    CurrencyDto targetCurrency;
    BigDecimal amount;
    BigDecimal expectedPrice;
    BigDecimal blockedBalance;
    boolean orderType;
    String status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdAt;
}
