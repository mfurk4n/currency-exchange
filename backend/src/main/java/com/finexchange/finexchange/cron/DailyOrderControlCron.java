package com.finexchange.finexchange.cron;

import com.finexchange.finexchange.service.ExchangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DailyOrderControlCron {
    private final ExchangeService exchangeService;

    //Haftaiçi sabah 9:30'da
    @Scheduled(cron = "0 30 9 * * MON-FRI", zone = "Europe/Istanbul")
    public void dailyLimitOrdersControl() {
        exchangeService.limitOrdersControl();
    }

    @Scheduled(cron = "0 30 9 * * MON-FRI", zone = "Europe/Istanbul")
    public void dailyStopOrdersControl() {
        exchangeService.stopOrdersControl();
    }


}
