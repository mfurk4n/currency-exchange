package com.finexchange.finexchange.mapper;


import com.finexchange.finexchange.dto.WalletDto;
import com.finexchange.finexchange.model.Wallet;


public class WalletDtoMapper {
    private WalletDtoMapper() {

    }

    public static WalletDto mapToWalletDto(Wallet wallet) {
        return WalletDto.builder()
                .id(wallet.getId())
                .customer(CustomerDtoMapper.mapToCustomerDto(wallet.getCustomer()))
                .currency(CurrencyDtoMapper.mapToCurrencyDto(wallet.getCurrency()))
                .balance(BalanceDtoMapper.mapToBalanceDto(wallet.getBalance()))
                .build();
    }


}
