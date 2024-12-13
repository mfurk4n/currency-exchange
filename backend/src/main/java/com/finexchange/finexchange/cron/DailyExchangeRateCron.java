package com.finexchange.finexchange.cron;

import com.finexchange.finexchange.dto.tcmbapi.TcmbExchangeRate;
import com.finexchange.finexchange.dto.tcmbapi.TcmbExchangeRateResponse;
import com.finexchange.finexchange.enums.ExchangeCode;
import com.finexchange.finexchange.service.ExchangeService;
import com.finexchange.finexchange.service.TcmbExchangeRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DailyExchangeRateCron {
    private final TcmbExchangeRateService tcmbExchangeRateService;
    private final ExchangeService exchangeService;

    //Haftaiçi sabah 6
    @Scheduled(cron = "0 0 6 * * MON-FRI", zone = "Europe/Istanbul")
    public void addDailyExchangeRates() {
        for (ExchangeCode code : ExchangeCode.values()) {
            TcmbExchangeRateResponse exchangeRateResponse = tcmbExchangeRateService.getSystemDayExchangeRate(code.toString());

            if (exchangeRateResponse != null
                    && exchangeRateResponse.getResult() != null
                    && exchangeRateResponse.getResult().getData() != null
                    && exchangeRateResponse.getResult().getData().getTcmbExchangeRateList() != null
                    && !exchangeRateResponse.getResult().getData().getTcmbExchangeRateList().isEmpty()
            ) {

                TcmbExchangeRate tcmbExchangeRate = exchangeRateResponse.getResult().getData().getTcmbExchangeRateList().get(0);
                exchangeService.addExchangeRate(tcmbExchangeRate, code.getBaseCurrency(), code.getTargetCurrency());

            }

        }

    }

}
