package com.finexchange.finexchange.mapper;

import com.finexchange.finexchange.dto.BalanceDto;
import com.finexchange.finexchange.model.Balance;

public class BalanceDtoMapper {
    private BalanceDtoMapper() {
    }

    public static BalanceDto mapToBalanceDto(Balance balance) {
        return BalanceDto.builder()
                .id(balance.getId())
                .amount(balance.getAmount())
                .loadedAmount(balance.getLoadedAmount())
                .dailyAmountChange(balance.getAmount().subtract(balance.getPreviousDayAmount()))
                .updatedAt(balance.getUpdatedAt())
                .build();
    }
}
