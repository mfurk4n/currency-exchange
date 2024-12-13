package com.finexchange.finexchange.dto.response;

import com.finexchange.finexchange.dto.OrderDto;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AllOrdersResponse {
    List<OrderDto> waiting;
    List<OrderDto> cancelled;
}
