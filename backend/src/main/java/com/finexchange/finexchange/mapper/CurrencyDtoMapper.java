package com.finexchange.finexchange.mapper;

import com.finexchange.finexchange.dto.CurrencyDto;
import com.finexchange.finexchange.model.Currency;

public final class CurrencyDtoMapper {

    private CurrencyDtoMapper() {

    }
    public static CurrencyDto mapToCurrencyDto(Currency currency) {
            return CurrencyDto.builder()
                    .id(currency.getId())
                    .code(currency.getCode())
                    .name(currency.getName())
                    .symbol(currency.getSymbol())
                    .build();
        }

}
