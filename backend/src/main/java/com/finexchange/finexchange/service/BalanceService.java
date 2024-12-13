package com.finexchange.finexchange.service;

import com.finexchange.finexchange.exception.BalanceNotFoundException;
import com.finexchange.finexchange.exception.IncorrectBalanceEntryException;
import com.finexchange.finexchange.exception.WalletNotFoundException;
import com.finexchange.finexchange.model.Balance;
import com.finexchange.finexchange.model.Wallet;
import com.finexchange.finexchange.repository.BalanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BalanceService {
    private final BalanceRepository balanceRepository;

    public Balance getBalanceById(String balanceId) {
        return balanceRepository.findById(balanceId)
                .orElseThrow(BalanceNotFoundException::new);
    }

    public Balance controlOrCreateBalanceEntity(Wallet wallet) {
        if (wallet == null)
            throw new WalletNotFoundException();
        if (wallet.getBalance() == null) {
            Balance newBalance = Balance.builder()
                    .amount(BigDecimal.valueOf(0))
                    .loadedAmount(BigDecimal.valueOf(0))
                    .previousDayAmount(BigDecimal.valueOf(0))
                    .wallet(wallet)
                    .updatedAt(LocalDateTime.now())
                    .build();

            return balanceRepository.save(newBalance);
        } else {
            return wallet.getBalance();
        }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public Balance updateBalanceWithWalletEntity(Wallet wallet, BigDecimal amount) {
        if (wallet == null)
            throw new WalletNotFoundException();

        if (wallet.getBalance() == null)
            throw new BalanceNotFoundException();

        if (amount.compareTo(BigDecimal.ZERO) < 0)
            throw new IncorrectBalanceEntryException();

        Balance balance = wallet.getBalance();
        balance.setAmount(amount);

        return balanceRepository.save(balance);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public Balance updatePreviousDayBalanceWithWalletEntity(Wallet wallet) {
        if (wallet == null)
            throw new WalletNotFoundException();

        if (wallet.getBalance() == null)
            throw new BalanceNotFoundException();

        Balance balance = wallet.getBalance();
        balance.setPreviousDayAmount(balance.getAmount());

        return balanceRepository.save(balance);
    }
}
