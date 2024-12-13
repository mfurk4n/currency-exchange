package com.finexchange.finexchange.service;

import com.finexchange.finexchange.exception.CurrencyNotFoundException;
import com.finexchange.finexchange.model.Currency;
import com.finexchange.finexchange.constant.CurrencyConstants;
import com.finexchange.finexchange.repository.CurrencyRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CurrencyService {
    private final CurrencyRepository currencyRepository;

    public Currency getCurrencyById(String currencyId) {
        return currencyRepository.findById(currencyId)
                .orElseThrow(CurrencyNotFoundException::new);
    }

    public Currency getCurrencyByCode(String code) {
        return currencyRepository.findByCode(code)
                .orElseThrow(CurrencyNotFoundException::new);
    }

    public List<Currency> getAllCurrencies() {
        return currencyRepository.findAllOrderByCreatedAtAsc();
    }

    @PostConstruct
    @Transactional(propagation = Propagation.REQUIRED)
    public void init() {
        if (currencyRepository.count() == 0) {
            for (int i = 0; i < CurrencyConstants.currencyCodes.size(); i++) {
                Currency currency = Currency.builder()
                        .code(CurrencyConstants.currencyCodes.get(i))
                        .name(CurrencyConstants.currencyNames.get(i))
                        .symbol(CurrencyConstants.currencySymbols.get(i))
                        .build();

                Currency saved = currencyRepository.save(currency);
                System.out.println(saved);
            }
        }
    }
}
