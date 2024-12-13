package com.finexchange.finexchange.dto.tcmbapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.List;

@lombok.Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Data {
    @JsonProperty("DovizFiyat")
    List<TcmbExchangeRate> tcmbExchangeRateList;

}
