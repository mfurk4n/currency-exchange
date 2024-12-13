package com.finexchange.finexchange.controller;


import com.finexchange.finexchange.dto.WalletDto;
import com.finexchange.finexchange.dto.request.CreateWalletRequest;
import com.finexchange.finexchange.dto.response.AvailableCurrencyResponse;
import com.finexchange.finexchange.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;


import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/v1/wallet")
@Tag(name = "Cüzdanla İlişikili APIler")
@RequiredArgsConstructor
public class WalletController {


    private final WalletService walletService;


    @GetMapping("/{walletId}")
    @Operation(summary = "ID'ye göre cüzdanı getirme")
    @ApiResponse(responseCode = "200", description = "Başarılı Response!")
    public ResponseEntity<WalletDto> getWalletDtoById(@PathVariable @NotBlank String walletId) {
        return ResponseEntity.ok(walletService.getWalletDtoById(walletId));
    }

    @GetMapping("all/{customerId}")
    @Operation(summary = "Müşteri ID'ye göre tüm cüzdanları listeleme")
    @ApiResponse(responseCode = "200", description = "Başarılı Response!")
    public ResponseEntity<List<WalletDto>> getAllWalletsByCustomerId(@PathVariable String customerId) {
        return ResponseEntity.ok(walletService.getAllWalletsDtoByCustomerId(customerId));
    }

    @PostMapping("/create")
    @Operation(summary = "Müşteri için cüzdan oluşturma servisi")
    @ApiResponse(responseCode = "200", description = "Başarılı Response!")
    public ResponseEntity<Void> createWallet(@Valid @RequestBody CreateWalletRequest createWalletRequest) {
        walletService.createWallet(createWalletRequest);
        return ResponseEntity.ok().build();
    }


    @PutMapping("/balance/{walletId}")
    @Operation(summary = "Cüzdanın bakiyesini ve tarihini güncelleme")
    @ApiResponse(responseCode = "200", description = "Başarılı Response!")
    public ResponseEntity<WalletDto> addBalanceToWallet(@PathVariable String walletId,
                                                        @RequestParam String customerId,
                                                        @RequestParam Optional<BigDecimal> newAmount) {

        return ResponseEntity.ok(walletService.addBalanceToWallet(walletId, customerId, newAmount));
    }


    @GetMapping("/available-currencies/{customerId}")
    @Operation(summary = "Müşterinin mevcut olmayan para birimlerini getirme")
    @ApiResponse(responseCode = "200", description = "Başarılı Response!")
    public ResponseEntity<List<AvailableCurrencyResponse>> getAvailableCurrencies(@PathVariable String customerId) {
        List<AvailableCurrencyResponse> availableCurrencies = walletService.getAvailableCurrenciesForCustomer(customerId);
        return ResponseEntity.ok(availableCurrencies);
    }

    @GetMapping("/balance-currency/{customerId}")
    @Operation(summary = "Cüzdanın bakiyesini ve para birimi bilgilerini getirme")
    @ApiResponse(responseCode = "200", description = "Başarılı Response!")
    public ResponseEntity<List<WalletDto>> getBalanceAndCurrencyFromWallet(@PathVariable String customerId) {
        return ResponseEntity.ok(walletService.getBalanceAndCurrencyFromWallet(customerId));
    }

    @GetMapping("/all-wallets")
    @Operation(summary = "Tüm cüzdanları listeleme admin için")
    @ApiResponse(responseCode = "200", description = "Başarılı Response!")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<WalletDto>> getAllWallets() {
        return ResponseEntity.ok(walletService.getAllWalletsDto());
    }
}
