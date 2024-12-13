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
public class TransactionDto {

    String id;
    CustomerDto customer;
    CurrencyDto currencyFrom;
    CurrencyDto currencyTo;
    BigDecimal amountFrom;
    BigDecimal amountTo;
    BigDecimal currencyPrice;
    String transactionType;
    String status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdAt;

}