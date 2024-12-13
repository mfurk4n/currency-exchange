package com.finexchange.finexchange.service;

import com.finexchange.finexchange.cache.UserContextCache;

import com.finexchange.finexchange.dto.ExchangeRateDto;
import com.finexchange.finexchange.dto.WalletDto;
import com.finexchange.finexchange.dto.request.CreateWalletRequest;
import com.finexchange.finexchange.dto.response.AvailableCurrencyResponse;
import com.finexchange.finexchange.exception.IncorrectBalanceEntryException;
import com.finexchange.finexchange.exception.UnauthorizedAccessException;
import com.finexchange.finexchange.exception.WalletAlreadyExistException;
import com.finexchange.finexchange.exception.WalletNotFoundException;


import com.finexchange.finexchange.mapper.WalletDtoMapper;
import com.finexchange.finexchange.model.*;
import com.finexchange.finexchange.repository.*;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class WalletService {
    private final WalletRepository walletRepository;
    private final UserContextCache userContextCache;
    private final CustomerService customerService;
    private final CurrencyService currencyService;
    private final BalanceService balanceService;
    private final ExchangeRateService exchangeRateService;

    public WalletService(WalletRepository walletRepository, UserContextCache userContextCache,
                         @Lazy CustomerService customerService, CurrencyService currencyService,
                         BalanceService balanceService, @Lazy ExchangeRateService exchangeRateService) {
        this.walletRepository = walletRepository;
        this.userContextCache = userContextCache;
        this.customerService = customerService;
        this.currencyService = currencyService;
        this.balanceService = balanceService;
        this.exchangeRateService = exchangeRateService;
    }

    public Wallet getWalletEntityById(String walletId) {
        return walletRepository.findById(walletId)
                .orElseThrow(WalletNotFoundException::new);
    }

    public Wallet getWalletEntityByCustomerIdAndCurrencyId(String customerId, String currencyId) {
        Currency currency = currencyService.getCurrencyById(currencyId);
        return walletRepository.findByCustomerIdAndCurrencyId(customerId, currency.getId())
                .orElseThrow(() -> new WalletNotFoundException(currency.getName()));
    }


    public List<Wallet> getAllWalletsEntityByCustomerId(String customerId) {
        return walletRepository.findByCustomerIdOrderByCreatedAtAsc(customerId);
    }

    public WalletDto getWalletDtoById(String walletId) {
        Wallet wallet = getWalletEntityById(walletId);
        return WalletDtoMapper.mapToWalletDto(wallet);
    }

    public List<WalletDto> getAllWalletsDtoByCustomerId(String customerId) {
        String targetCustomerId = (customerId == null || customerId.isBlank())
                ? userContextCache.getCurrentUser().getId()
                : customerService.getCustomerEntityById(customerId).getId();

        List<Wallet> walletList = getAllWalletsEntityByCustomerId(targetCustomerId);
        List<ExchangeRateDto> exchangeRates = exchangeRateService.getDirectRates();

        List<WalletDto> walletDtoList = walletList.stream()
                .map(wallet -> {

                    WalletDto walletDto = WalletDtoMapper.mapToWalletDto(wallet);
                    if (wallet.getCurrency().getCode().equals("TRY")) {
                        walletDto.getBalance().setTryAmount(walletDto.getBalance().getAmount());
                    }
                    return walletDto;
                }).toList();

        List<WalletDto> updatedWalletDtoList = walletDtoList.stream().map(walletDto -> {
            exchangeRates.stream()
                    .filter(exchangeRate -> exchangeRate.getBaseCurrency().getId().equals(walletDto.getCurrency().getId()))
                    .forEach(matchingExchangeRate -> {
                        walletDto.getBalance().setTryAmount(walletDto.getBalance().getAmount().multiply(matchingExchangeRate.getBid()));
                    });
            return walletDto;
        }).collect(Collectors.toList());

        return updatedWalletDtoList;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public WalletDto addBalanceToWallet(String walletId, String customerId, Optional<BigDecimal> newAmount) {
        Customer customer = customerService.getCustomerEntityById(customerId);

        if (!newAmount.isPresent() || newAmount.get().compareTo(BigDecimal.ZERO) < 0) {
            throw new IncorrectBalanceEntryException();
        }

        BigDecimal newAmountValue = newAmount.get();

        Wallet existingWallet = walletRepository.findById(walletId)
                .orElseThrow(WalletNotFoundException::new);

        if (!existingWallet.getCustomer().getId().equals(customer.getId())) {
            throw new UnauthorizedAccessException();
        }
        Balance balance = existingWallet.getBalance();

        balance.setAmount(newAmountValue.add(balance.getAmount()));
        balance.setAmount(newAmountValue.add(balance.getLoadedAmount()));
        balance.setUpdatedAt(LocalDateTime.now());

        Wallet updatedWallet = walletRepository.save(existingWallet);

        return WalletDtoMapper.mapToWalletDto(updatedWallet);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void createWallet(CreateWalletRequest createWalletRequest) {
        Optional<Wallet> optWallet = walletRepository
                .findByCustomerIdAndCurrencyId(createWalletRequest.getCustomerId(), createWalletRequest.getCurrencyId());
        if (optWallet.isPresent()) {
            throw new WalletAlreadyExistException();
        } else {
            Wallet newWallet = Wallet.builder()
                    .customer(customerService.getCustomerEntityById(createWalletRequest.getCustomerId()))
                    .currency(currencyService.getCurrencyById(createWalletRequest.getCurrencyId()))
                    .createdAt(LocalDateTime.now())
                    .build();
            Wallet savedWallet = walletRepository.save(newWallet);
            savedWallet.setBalance(balanceService.controlOrCreateBalanceEntity(savedWallet));
            walletRepository.save(savedWallet);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void dailyBalanceCalculate() {
        List<Wallet> walletList = walletRepository.findAll();
        walletList.forEach(balanceService::updatePreviousDayBalanceWithWalletEntity);
    }

    public List<AvailableCurrencyResponse> getAvailableCurrenciesForCustomer(String customerId) {
        // Customer'a ait mevcut cüzdanları bulma
        List<Currency> customerCurrencies = walletRepository.findAllByCustomerId(customerId)
                .stream()
                .map(wallet -> wallet.getCurrency())
                .collect(Collectors.toList());

        // Tüm döviz türlerini bulma
        List<Currency> allCurrencies = currencyService.getAllCurrencies();

        // Customer'ın sahip olmadığı dövizleri filtreleme
        List<Currency> availableCurrencies = allCurrencies.stream()
                .filter(currency -> !customerCurrencies.contains(currency))
                .collect(Collectors.toList());

        // AvailableCurrencyResponse objesine dönüştürme
        return availableCurrencies.stream()
                .map(currency -> {
                    AvailableCurrencyResponse response = new AvailableCurrencyResponse();
                    response.setCurrencyId(currency.getId());
                    response.setCurrencyName(currency.getName());
                    return response;
                })
                .collect(Collectors.toList());
    }

    public List<WalletDto> getBalanceAndCurrencyFromWallet(String customerId) {

        List<Wallet> walletList = walletRepository.findByCustomerIdOrderByCreatedAtAsc(customerId);
        List<ExchangeRateDto> exchangeRates = exchangeRateService.getDirectRates();

        List<WalletDto> walletDtoList = walletList.stream()
                .map(wallet -> {
                    WalletDto walletDto = WalletDtoMapper.mapToWalletDto(wallet);
                    if (wallet.getCurrency().getCode().equals("TRY")) {
                        walletDto.getBalance().setTryAmount(walletDto.getBalance().getAmount());
                    }
                    return walletDto;
                }).toList();

        List<WalletDto> updatedWalletDtoList = walletDtoList.stream().map(walletDto -> {
            exchangeRates.stream()
                    .filter(exchangeRate -> exchangeRate.getBaseCurrency().getId().equals(walletDto.getCurrency().getId()))
                    .forEach(matchingExchangeRate -> {
                        walletDto.getBalance().setTryAmount(walletDto.getBalance().getAmount().multiply(matchingExchangeRate.getBid()));
                    });
            return walletDto;
        }).collect(Collectors.toList());

        return updatedWalletDtoList;
    }

    public List<WalletDto> getAllWalletsDto() {
        List<Wallet> walletList = walletRepository.findAll();
        List<ExchangeRateDto> exchangeRates = exchangeRateService.getDirectRates();

        List<WalletDto> walletDtoList = walletList.stream()
                .map(wallet -> {
                    WalletDto walletDto = WalletDtoMapper.mapToWalletDto(wallet);
                    if (wallet.getCurrency().getCode().equals("TRY")) {
                        walletDto.getBalance().setTryAmount(walletDto.getBalance().getAmount());
                    }
                    return walletDto;
                }).toList();

        List<WalletDto> updatedWalletDtoList = walletDtoList.stream().map(walletDto -> {
            exchangeRates.stream()
                    .filter(exchangeRate -> exchangeRate.getBaseCurrency().getId().equals(walletDto.getCurrency().getId()))
                    .forEach(matchingExchangeRate -> {
                        walletDto.getBalance().setTryAmount(walletDto.getBalance().getAmount().multiply(matchingExchangeRate.getBid()));
                    });
            return walletDto;
        }).collect(Collectors.toList());

        return updatedWalletDtoList;
    }

}
