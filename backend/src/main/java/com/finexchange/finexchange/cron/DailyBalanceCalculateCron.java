package com.finexchange.finexchange.cron;

import com.finexchange.finexchange.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DailyBalanceCalculateCron {
    private final WalletService walletService;

    //Haftaiçi her akşam 18'de
    @Scheduled(cron = "0 0 18 * * MON-FRI", zone = "Europe/Istanbul")
    public void dailyBalanceCalculate() {
        walletService.dailyBalanceCalculate();
    }
}
