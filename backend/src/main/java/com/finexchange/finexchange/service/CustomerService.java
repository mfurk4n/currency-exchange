package com.finexchange.finexchange.service;

import com.finexchange.finexchange.cache.UserContextCache;
import com.finexchange.finexchange.dto.CustomerDto;
import com.finexchange.finexchange.dto.request.CreateCustomerRequest;
import com.finexchange.finexchange.dto.request.CreateWalletRequest;
import com.finexchange.finexchange.dto.response.CustomerSelectResponse;
import com.finexchange.finexchange.exception.*;
import com.finexchange.finexchange.mapper.CustomerDtoMapper;
import com.finexchange.finexchange.mapper.UserDtoMapper;
import com.finexchange.finexchange.model.Customer;
import com.finexchange.finexchange.model.User;
import com.finexchange.finexchange.repository.CustomerRepository;
import com.finexchange.finexchange.security.JwtTokenUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final UserContextCache userContextCache;
    private final JwtTokenUtils jwtTokenUtils;
    private final UserService userService;
    private final CurrencyService currencyService;
    private final WalletService walletService;

    public CustomerService(CustomerRepository customerRepository, UserContextCache userContextCache,
                           JwtTokenUtils jwtTokenUtils, UserService userService,
                           CurrencyService currencyService, @Lazy WalletService walletService) {
        this.customerRepository = customerRepository;
        this.userContextCache = userContextCache;
        this.jwtTokenUtils = jwtTokenUtils;
        this.userService = userService;
        this.currencyService = currencyService;
        this.walletService = walletService;
    }

    public Customer getCustomerEntityById(String customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(CustomerNotFoundException::new);
    }

    public List<Customer> getAllCustomersEntityByUserId(String userId) {
        return customerRepository.findByUserId(userId);
    }

    public CustomerDto getCustomerDtoById(String customerId) {
        Customer customer = getCustomerEntityById(customerId);
        return CustomerDtoMapper.mapToCustomerDto(customer);
    }

    public List<CustomerDto> getAllCustomersDtoByUserId(Optional<String> userId) {
        if (userId.isEmpty()) {
            List<Customer> customerList = getAllCustomersEntityByUserId(userContextCache.getCurrentUser().getId());
            return customerList.stream()
                    .map(CustomerDtoMapper::mapToCustomerDto)
                    .collect(Collectors.toList());
        } else {
            User user = userService.getUserEntityById(userId.get());
            List<Customer> customerList = getAllCustomersEntityByUserId(user.getId());
            return customerList.stream()
                    .map(CustomerDtoMapper::mapToCustomerDto)
                    .collect(Collectors.toList());
        }
    }

    public CustomerSelectResponse selectCustomerById(String customerId) {
        Customer customer = getCustomerEntityById(customerId);
        if (!userContextCache.getCurrentUser().getId().equals(customer.getUser().getId()))
            throw new InsufficientAuthorityException();

        String jwtToken = jwtTokenUtils.generateJwtTokenByUserIdAndCustomerId(userContextCache.getCurrentUser().getId(), customer.getId());
        CustomerSelectResponse customerSelectResponse = new CustomerSelectResponse();
        customerSelectResponse.setCustomer(CustomerDtoMapper.mapToCustomerDto(customer));
        customerSelectResponse.setJwtToken(jwtToken);

        return customerSelectResponse;
    }

    public CustomerDto createCustomer(CreateCustomerRequest request) {
        String currentUserId = userContextCache.getCurrentUser().getId();
        String customerName = request.getName();

        if (customerRepository.existsByNationalIdOrTaxId(request.getNationalId(), request.getTaxId())) {
            throw new CustomerAlreadyExistsException();
        }

        String nationalId = request.getNationalId();
        String taxId = request.getTaxId();

        if (request.isLegal()) {
            taxId = null;
            if (nationalId == null || nationalId.isBlank() || nationalId.length() != 11) {
                throw new InvalidCustomerNationalIdException();
            }
        } else {
            nationalId = null;
            if (taxId == null || taxId.isBlank() || taxId.length() != 10) {
                throw new InvalidCustomerTaxIdException();
            }
        }

        Customer customer = Customer.builder()
                .name(customerName)
                .user(userService.getUserEntityById(currentUserId))
                .isLegal(request.isLegal())
                .nationalId(nationalId)
                .taxId(taxId)
                .createdAt(LocalDateTime.now())
                .build();

        Customer savedCustomer = customerRepository.save(customer);

        CreateWalletRequest walletRequest = new CreateWalletRequest(customer.getId(), currencyService.getCurrencyByCode("TRY").getId());
        walletService.createWallet(walletRequest);

        return CustomerDto.builder()
                .id(savedCustomer.getId())
                .name(savedCustomer.getName())
                .user(UserDtoMapper.mapToUserDto(savedCustomer.getUser())) // UserDto'yu haritalayın
                .isLegal(savedCustomer.isLegal())
                .nationalId(savedCustomer.getNationalId())
                .taxId(savedCustomer.getTaxId())
                .createdAt(savedCustomer.getCreatedAt())
                .build();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Customer saveCustomer(Customer customer) {
        if (customer == null)
            throw new CustomerNotFoundException();
        return customerRepository.save(customer);
    }
}