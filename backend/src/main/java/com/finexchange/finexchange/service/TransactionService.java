package com.finexchange.finexchange.service;

import com.finexchange.finexchange.constant.TransactionConstants;
import com.finexchange.finexchange.dto.CurrencyDto;
import com.finexchange.finexchange.dto.CustomerDto;
import com.finexchange.finexchange.dto.TransactionDto;
import com.finexchange.finexchange.exception.TransactionNotFoundException;
import com.finexchange.finexchange.mapper.CurrencyDtoMapper;
import com.finexchange.finexchange.mapper.CustomerDtoMapper;
import com.finexchange.finexchange.mapper.TransactionDtoMapper;
import com.finexchange.finexchange.model.Transaction;
import com.finexchange.finexchange.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final CustomerService customerService;

    public Transaction getTransactionEntityById(String transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(TransactionNotFoundException::new);
    }

    public List<TransactionDto> getTransactionsByStatusAndUserId(String userId, Optional<String> status, Optional<Integer> page, Optional<Integer> size) {
        userService.getUserEntityById(userId);
        Pageable pageable = createPageable(page, size);
        List<Transaction> transactions;

        if (status.isPresent()) {
            transactions = transactionRepository.findByUserIdAndStatus(userId, status.get().toString());
        } else {
            transactions = transactionRepository.findByUserId(userId);
        }

        return mapTransactionsToDtos(transactions);
    }

    public List<TransactionDto> getTransactionsByStatusAndCustomerId(String customerId, Optional<String> status, Optional<Integer> page, Optional<Integer> size) {
        customerService.getCustomerEntityById(customerId);
        Pageable pageable = createPageable(page, size);
        List<Transaction> transactions;

        if (status.isPresent()) {
            transactions = transactionRepository.findByCustomerIdAndStatus(customerId, status.get().toString());
        } else {
            transactions = transactionRepository.findByCustomerId(customerId);
        }

        return mapTransactionsToDtos(transactions);
    }

    public List<TransactionDto> getTransactionsByStatus(Optional<String> status, Optional<Integer> page, Optional<Integer> size) {
        Pageable pageable = createPageable(page, size);
        List<Transaction> transactions;

        if (status.isPresent()) {
            transactions = transactionRepository.findByStatus(status.get().toString());
        } else {
            transactions = transactionRepository.findAll(pageable).getContent();
        }

        return mapTransactionsToDtos(transactions);
    }

    private TransactionDto mapTransactionToDto(Transaction transaction) {
        CustomerDto customerDto = CustomerDtoMapper.mapToCustomerDto(transaction.getCustomer());
        CurrencyDto currencyFromDto = CurrencyDtoMapper.mapToCurrencyDto(transaction.getCurrencyFrom());
        CurrencyDto currencyToDto = CurrencyDtoMapper.mapToCurrencyDto(transaction.getCurrencyTo());
        return TransactionDtoMapper.maptoTransactionDto(transaction, currencyFromDto, currencyToDto, customerDto);
    }

    private List<TransactionDto> mapTransactionsToDtos(List<Transaction> transactions) {
        return transactions.stream()
                .map(this::mapTransactionToDto)
                .collect(Collectors.toList());
    }

    private Pageable createPageable(Optional<Integer> page, Optional<Integer> size) {
        return PageRequest.of(page.orElse(0), size.orElse(10));
    }

    public List<Transaction> getAllWaitStatusTransactionEntities() {
        return transactionRepository.findByStatus(TransactionConstants.TSX_WAIT);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public Transaction saveTransaction(Transaction transaction) {
        if (transaction == null)
            throw new TransactionNotFoundException();
        return transactionRepository.save(transaction);
    }


}