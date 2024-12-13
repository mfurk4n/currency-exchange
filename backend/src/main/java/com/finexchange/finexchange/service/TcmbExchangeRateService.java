package com.finexchange.finexchange.service;

import com.finexchange.finexchange.dto.tcmbapi.TcmbExchangeRateResponse;
import com.finexchange.finexchange.util.DateUtils;
import lombok.RequiredArgsConstructor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
@RequiredArgsConstructor
public class TcmbExchangeRateService {
    @Value("${currency.api.url}")
    private String API_URL;
    @Value("${currency.api.key}")
    private String API_KEY;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TcmbExchangeRateResponse getTcmbExchangeRateResponse(String dataDate, String assetCode) {
        String url = API_URL + assetCode + "&data_date=" + dataDate + "&api_key=" + API_KEY;
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);

        try {
            if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
                TcmbExchangeRateResponse responseBody = objectMapper.readValue(responseEntity.getBody(), TcmbExchangeRateResponse.class);

                return responseBody;
            } else {
                return null;
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public TcmbExchangeRateResponse getSystemDayExchangeRate(String assetCode) {
        return getTcmbExchangeRateResponse(DateUtils.getSystemDateAsDay(), assetCode);
    }

}
