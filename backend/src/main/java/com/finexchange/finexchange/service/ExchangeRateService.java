package com.finexchange.finexchange.service;

import com.finexchange.finexchange.dto.ExchangeRateDto;
import com.finexchange.finexchange.dto.response.*;
import com.finexchange.finexchange.dto.response.chart.ChartResponse;
import com.finexchange.finexchange.dto.response.chart.ExchangeRatesForBaseCurrency;
import com.finexchange.finexchange.dto.response.chart.ExchangeRatesMinimal;
import com.finexchange.finexchange.dto.response.chart.WeeklyExchangeRatesAsPartResponse;
import com.finexchange.finexchange.exception.ExchangeRateNotFoundException;
import com.finexchange.finexchange.mapper.ExchangeRateDtoMapper;
import com.finexchange.finexchange.model.Currency;
import com.finexchange.finexchange.model.ExchangeRate;
import com.finexchange.finexchange.repository.ExchangeRateRepository;

import com.finexchange.finexchange.util.DateUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import java.util.Map;
import java.util.stream.Collectors;


@Service
public class ExchangeRateService {
    private final ExchangeRateRepository exchangeRateRepository;
    private final WalletService walletService;
    private final CurrencyService currencyService;

    public ExchangeRateService(ExchangeRateRepository exchangeRateRepository, @Lazy WalletService walletService, CurrencyService currencyService) {
        this.exchangeRateRepository = exchangeRateRepository;
        this.walletService = walletService;
        this.currencyService = currencyService;
    }

    public ExchangeRate getLastExchangeRateByBaseAndTargetId(String baseCurrencyId, String targetCurrencyId) {
        return exchangeRateRepository.findTopByBaseCurrencyIdAndTargetCurrencyIdOrderByCreatedAtDesc(baseCurrencyId, targetCurrencyId)
                .orElseThrow(ExchangeRateNotFoundException::new);
    }

    public List<ExchangeRateDto> getDirectRates() {
        List<ExchangeRateDto> allRates = getAllRates();
        return filterDirectRates(allRates);
    }

    public List<ExchangeRateDto> getCrossRates() {
        List<ExchangeRateDto> allRates = getAllRates();
        return filterCrossRates(allRates);
    }

    public List<ExchangeRateDto> getAllRates() {
        List<ExchangeRate> entities = exchangeRateRepository.findLatestExchangeRates();
        String dataDate = entities.get(0).getDataDate();
        List<ExchangeRate> entitiesPreviousWeekdayList;

        do {
            dataDate = DateUtils.getPreviousWeekday(dataDate);
            entitiesPreviousWeekdayList = exchangeRateRepository.findLatestExchangeRatesByDataDate(dataDate);
        } while (entitiesPreviousWeekdayList.isEmpty());


        List<ExchangeRateDto> rateDtos = new ArrayList<>();

        for (int i = 0; i < entities.size(); i++) {
            BigDecimal todayAsk = entities.get(i).getAsk();
            BigDecimal yesterdayAsk = entitiesPreviousWeekdayList.get(i).getAsk();

            BigDecimal diff = todayAsk.subtract(yesterdayAsk);

            BigDecimal diffDiv = diff.divide(yesterdayAsk, 5, RoundingMode.HALF_UP);

            BigDecimal changeRate = diffDiv.multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP);

            ExchangeRateDto dto = ExchangeRateDtoMapper.mapToExchangeRateDto(entities.get(i), changeRate);

            rateDtos.add(dto);
        }

        return rateDtos;
    }


    public AllExchangeRatesResponse getAllRatesAsPart() {
        AllExchangeRatesResponse allExchangeRatesResponse = new AllExchangeRatesResponse();
        allExchangeRatesResponse.setCross(getCrossRates());
        allExchangeRatesResponse.setNoncross(getDirectRates());
        return allExchangeRatesResponse;
    }

    private List<ExchangeRateDto> filterDirectRates(List<ExchangeRateDto> rates) {
        // Sağda TRY olan kurları doğrudan kurlar olarak kabul ediyoruz
        return rates.stream()
                .filter(rate -> rate.getTargetCurrency().getCode().equals("TRY"))
                .collect(Collectors.toList());
    }

    private List<ExchangeRateDto> filterCrossRates(List<ExchangeRateDto> rates) {
        // Sağda TRY olan kurlar hariç tüm kurları döndür
        return rates.stream()
                .filter(rate -> !rate.getTargetCurrency().getCode().equals("TRY"))
                .collect(Collectors.toList());
    }


    public ExchangeRatesWithWalletsResponse getAllRatesAsPartWithCustomerWallets(String customerId) {
        ExchangeRatesWithWalletsResponse response = new ExchangeRatesWithWalletsResponse();
        response.setExchangeRates(getAllRatesAsPart());
        response.setWallets(walletService.getAllWalletsDtoByCustomerId(customerId));

        return response;
    }

    public WeeklyExchangeRatesAsPartResponse getWeeklyExchangeRatesAsPartBaseTRY() {
        Currency currency = currencyService.getCurrencyByCode("TRY");
        List<ExchangeRate> exchangeRateList = exchangeRateRepository
                .getWeeklyExchangeRatesAsPartBaseTargetCurrency(DateUtils.getLast7WeekdaysIncludingToday(), currency.getId());

        // baseCurrencylere göre exchangeRateleri gruplama ve dataDate'e göre sıralama
        Map<String, List<ExchangeRatesMinimal>> groupedByBaseCurrency = exchangeRateList.stream()
                .collect(Collectors.groupingBy(
                        exchangeRate -> exchangeRate.getBaseCurrency().getCode(),
                        Collectors.mapping(
                                exchangeRate -> new ExchangeRatesMinimal(exchangeRate.getAsk(), exchangeRate.getBid(), exchangeRate.getDataDate()),
                                Collectors.collectingAndThen(
                                        Collectors.toList(),
                                        list -> list.stream()
                                                .sorted(Comparator.comparing(ExchangeRatesMinimal::getDataDate))
                                                .collect(Collectors.toList())
                                )
                        )
                ));

        List<ExchangeRatesForBaseCurrency> baseCurrencies = groupedByBaseCurrency.entrySet().stream()
                .map(entry -> new ExchangeRatesForBaseCurrency(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        return new WeeklyExchangeRatesAsPartResponse(currency.getCode(), baseCurrencies);
    }

    public WeeklyExchangeRatesAsPartResponse getWeeklyExchangeRatesAsPartBaseUSD() {
        Currency currency = currencyService.getCurrencyByCode("USD");
        List<ExchangeRate> exchangeRateList = exchangeRateRepository
                .getWeeklyExchangeRatesAsPartBaseBaseCurrency(DateUtils.getLast7WeekdaysIncludingToday(), currency.getId());

        // targetCurrency'leri gruplama ve dataDate'e göre sıralama
        Map<String, List<ExchangeRatesMinimal>> groupedByTargetCurrency = exchangeRateList.stream()
                .filter(exchangeRate -> !exchangeRate.getTargetCurrency().getCode().equals("TRY"))
                .collect(Collectors.groupingBy(
                        exchangeRate -> exchangeRate.getTargetCurrency().getCode(),
                        Collectors.mapping(
                                exchangeRate -> new ExchangeRatesMinimal(exchangeRate.getAsk(), exchangeRate.getBid(), exchangeRate.getDataDate()),
                                Collectors.collectingAndThen(
                                        Collectors.toList(),
                                        list -> list.stream()
                                                .sorted(Comparator.comparing(ExchangeRatesMinimal::getDataDate))
                                                .collect(Collectors.toList())
                                )
                        )
                ));

        List<ExchangeRatesForBaseCurrency> targetCurrencies = groupedByTargetCurrency.entrySet().stream()
                .map(entry -> new ExchangeRatesForBaseCurrency(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        return new WeeklyExchangeRatesAsPartResponse(currency.getCode(), targetCurrencies);
    }


    public ChartResponse getForChartWeeklyExchangeRatesAsPart() {
        ChartResponse chartResponse = new ChartResponse();

        chartResponse.setNoncross(getWeeklyExchangeRatesAsPartBaseTRY());
        chartResponse.setCross(getWeeklyExchangeRatesAsPartBaseUSD());

        return chartResponse;
    }


}