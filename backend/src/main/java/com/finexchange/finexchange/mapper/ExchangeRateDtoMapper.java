package com.finexchange.finexchange.mapper;

import com.finexchange.finexchange.dto.ExchangeRateDto;
import com.finexchange.finexchange.model.ExchangeRate;


import java.math.BigDecimal;

public final class ExchangeRateDtoMapper {

    private ExchangeRateDtoMapper() {
        // Private constructor to prevent instantiation
    }

    public static ExchangeRateDto mapToExchangeRateDto(ExchangeRate exchangeRate, BigDecimal changeRate) {
        return ExchangeRateDto.builder()
                .id(exchangeRate.getId())
                .exchangeCode(exchangeRate.getExchangeCode())
                .ask(exchangeRate.getAsk())
                .bid(exchangeRate.getBid())
                .baseCurrency(CurrencyDtoMapper.mapToCurrencyDto(exchangeRate.getBaseCurrency()))
                .targetCurrency(CurrencyDtoMapper.mapToCurrencyDto(exchangeRate.getTargetCurrency()))
                .dataDate(exchangeRate.getDataDate())
                .changeRate(changeRate)
                .build();
    }


}
