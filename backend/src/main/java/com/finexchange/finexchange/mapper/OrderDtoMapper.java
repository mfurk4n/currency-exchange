package com.finexchange.finexchange.mapper;

import com.finexchange.finexchange.dto.OrderDto;
import com.finexchange.finexchange.model.Order;

public final class OrderDtoMapper {
    private OrderDtoMapper() {
    }

    public static OrderDto mapToOrderDto(Order order) {
        return OrderDto.builder()
                .id(order.getId())
                .customer(CustomerDtoMapper.mapToCustomerDto(order.getCustomer()))
                .baseCurrency(CurrencyDtoMapper.mapToCurrencyDto(order.getBaseCurrency()))
                .targetCurrency(CurrencyDtoMapper.mapToCurrencyDto(order.getTargetCurrency()))
                .amount(order.getAmount())
                .expectedPrice(order.getExpectedPrice())
                .blockedBalance(order.getBlockedBalance())
                .orderType(order.isOrderType())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
