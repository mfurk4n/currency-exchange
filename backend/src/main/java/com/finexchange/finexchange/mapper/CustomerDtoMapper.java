package com.finexchange.finexchange.mapper;

import com.finexchange.finexchange.dto.CustomerDto;
import com.finexchange.finexchange.model.Customer;

public final class CustomerDtoMapper {

    private CustomerDtoMapper() {
    }

    public static CustomerDto mapToCustomerDto(Customer customer) {
        return CustomerDto.builder()
                .id(customer.getId())
                .name(customer.getName())
                .createdAt(customer.getCreatedAt())
                .isLegal(customer.isLegal())
                .taxId(customer.getTaxId())
                .nationalId(customer.isLegal() ? customer.getNationalId() : "-")
                .user(UserDtoMapper.mapToUserDto(customer.getUser()))
                .build();
    }
}
