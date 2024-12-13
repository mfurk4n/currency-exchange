package com.finexchange.finexchange.dto.response;

import com.finexchange.finexchange.dto.ExchangeRateDto;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AllExchangeRatesResponse {
    List<ExchangeRateDto> cross;
    List<ExchangeRateDto> noncross;

}
