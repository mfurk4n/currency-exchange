package com.finexchange.finexchange.controller;

import com.finexchange.finexchange.dto.ExchangeRateDto;
import com.finexchange.finexchange.dto.request.ExchangeOrderRequest;
import com.finexchange.finexchange.dto.response.AllExchangeRatesResponse;
import com.finexchange.finexchange.dto.response.ExchangeRatesWithWalletsResponse;
import com.finexchange.finexchange.dto.response.chart.ChartResponse;
import com.finexchange.finexchange.service.ExchangeRateService;
import com.finexchange.finexchange.service.ExchangeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/exchange")
@Tag(name = "Döviz Kuru Değişimi İlişkiliş APIler")
@RequiredArgsConstructor
public class ExchangeController {
    private final ExchangeService exchangeService;
    private final ExchangeRateService exchangeRateService;

    @GetMapping("/all")
    @Operation(summary = "Tüm döviz kurlarını döner")
    @ApiResponse(responseCode = "200", description = "Başarılı Response!")
    public List<ExchangeRateDto> getAllExchangeRates() {
        return exchangeRateService.getAllRates();
    }

    @GetMapping()
    @Operation(summary = "Çapraz kurları döner")
    @ApiResponse(responseCode = "200", description = "Başarılı Response!")
    public List<ExchangeRateDto> getCrossExchangeRates() {
        return exchangeRateService.getCrossRates();
    }

    @GetMapping("/all-part")
    @Operation(summary = "Çapraz ve çapraz olmayan kurları ayırarak döner")
    @ApiResponse(responseCode = "200", description = "Başarılı Response!")
    public AllExchangeRatesResponse getAllRatesAsPart() {
        return exchangeRateService.getAllRatesAsPart();
    }

    @GetMapping("/all-part-wallets/{customerId}")
    @Operation(summary = "Çapraz ve çapraz olmayan kurları ayırarak döner")
    @ApiResponse(responseCode = "200", description = "Başarılı Response!")
    public ExchangeRatesWithWalletsResponse getAllRatesAsPartWithCustomerWallets(@PathVariable String customerId) {
        return exchangeRateService.getAllRatesAsPartWithCustomerWallets(customerId);
    }

    @PostMapping("/order")
    @Operation(summary = "Döviz alım-satım emirlerini işler")
    @ApiResponse(responseCode = "200", description = "Başarılı Response!")
    public ResponseEntity<Void> createExchangeOrder(@Valid @RequestBody ExchangeOrderRequest exchangeOrderRequest) {
        exchangeService.createExchangeOrder(exchangeOrderRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/chart")
    @Operation(summary = "Grafik için haftalık kur değerlerini json düzenlemesiyle gönderir")
    @ApiResponse(responseCode = "200", description = "Başarılı Response!")
    public ChartResponse getForChartWeeklyExchangeRatesAsPart() {
        return exchangeRateService.getForChartWeeklyExchangeRatesAsPart();
    }


}
