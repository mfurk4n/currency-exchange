package com.finexchange.finexchange.controller;

import com.finexchange.finexchange.dto.TransactionDto;
import com.finexchange.finexchange.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/transaction")
@Tag(name = "Transaction Related APIs")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/all/user/{userId}")
    @Operation(summary = "User'a ait tüm işlemleri döner")
    @ApiResponse(responseCode = "200", description = "Başarılı Response!")
    public ResponseEntity<List<TransactionDto>> getAllTransactionsByUserId(@PathVariable String userId,
                                                                           @RequestParam Optional<String> status,
                                                                           @RequestParam Optional<Integer> page,
                                                                           @RequestParam Optional<Integer> size) {
        return ResponseEntity.ok(transactionService.getTransactionsByStatusAndUserId(userId, status, page, size));
    }

    @GetMapping("/all/customer/{customerId}")
    @Operation(summary = "Customer'a ait tüm işlemleri döner")
    @ApiResponse(responseCode = "200", description = "Başarılı Response!")
    public ResponseEntity<List<TransactionDto>> getAllTransactionsByCustomerId(@PathVariable String customerId,
                                                                               @RequestParam Optional<String> status,
                                                                               @RequestParam Optional<Integer> page,
                                                                               @RequestParam Optional<Integer> size) {
        return ResponseEntity.ok(transactionService.getTransactionsByStatusAndCustomerId(customerId, status, page, size));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/all-users")
    @Operation(summary = "Admin için tüm işlemleri döner")
    @ApiResponse(responseCode = "200", description = "Başarılı Response!")
    public ResponseEntity<List<TransactionDto>> getAllTransactions(@RequestParam Optional<String> status,
                                                                   @RequestParam Optional<Integer> page,
                                                                   @RequestParam Optional<Integer> size) {
        return ResponseEntity.ok(transactionService.getTransactionsByStatus(status, page, size));
    }
}
