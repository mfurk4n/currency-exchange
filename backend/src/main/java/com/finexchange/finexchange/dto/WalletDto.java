package com.finexchange.finexchange.dto;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletDto {

    String id;
    CustomerDto customer;
    CurrencyDto currency;
    BalanceDto balance;
}

