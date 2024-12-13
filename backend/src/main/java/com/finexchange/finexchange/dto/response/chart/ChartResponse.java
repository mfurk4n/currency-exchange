package com.finexchange.finexchange.dto.response.chart;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class ChartResponse {
    WeeklyExchangeRatesAsPartResponse noncross;
    WeeklyExchangeRatesAsPartResponse cross;
}
