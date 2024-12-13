package com.finexchange.finexchange.dto.response;

import com.finexchange.finexchange.dto.WalletDto;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExchangeRatesWithWalletsResponse {
    AllExchangeRatesResponse exchangeRates;
    List<WalletDto> wallets;

}
