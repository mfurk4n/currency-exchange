package com.finexchange.finexchange.dto.tcmbapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TcmbExchangeRate {
    @JsonProperty("record_id")
    String recordId;
    @JsonProperty("record_date")
    String recordDate;
    @JsonProperty("asset_name")
    String assetName;
    @JsonProperty("asset_code")
    String assetCode;
    @JsonProperty("ask")
    BigDecimal ask;
    @JsonProperty("bid")
    BigDecimal bid;
    @JsonProperty("data_date")
    String dataDate;
    @JsonProperty("market_code")
    String marketCode;
}
