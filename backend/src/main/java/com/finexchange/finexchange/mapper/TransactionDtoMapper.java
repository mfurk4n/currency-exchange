package com.finexchange.finexchange.mapper;

import com.finexchange.finexchange.dto.CurrencyDto;
import com.finexchange.finexchange.dto.CustomerDto;
import com.finexchange.finexchange.dto.TransactionDto;
import com.finexchange.finexchange.model.Transaction;

import java.time.format.DateTimeFormatter;

public final class TransactionDtoMapper {

    private TransactionDtoMapper() {
    }
    public static TransactionDto maptoTransactionDto(Transaction transaction, CurrencyDto currencyFromDto, CurrencyDto currencyToDto, CustomerDto customerDto) {
        return TransactionDto.builder()
                .id(transaction.getId())
                .customer(customerDto)
                .currencyFrom(currencyFromDto)
                .currencyTo(currencyToDto)
                .currencyPrice(transaction.getCurrencyPrice())
                .amountFrom(transaction.getAmountFrom())
                .amountTo(transaction.getAmountTo())
                .transactionType(transaction.getTransactionType())
                .status(transaction.getStatus())
                .createdAt(transaction.getCreatedAt())
                .build();
    }

}
