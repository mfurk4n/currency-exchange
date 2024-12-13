package com.finexchange.finexchange.controller;

import com.finexchange.finexchange.dto.CustomerDto;
import com.finexchange.finexchange.dto.request.CreateCustomerRequest;
import com.finexchange.finexchange.dto.response.CustomerSelectResponse;
import com.finexchange.finexchange.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/customer")
@Tag(name = "Müşteri İlişkili APIler")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/{customerId}")
    @Operation(summary = "Tek müşteri bilgisini döner")
    @ApiResponse(responseCode = "200", description = "Başarılı Response!")
    public ResponseEntity<CustomerDto> getCustomerDtoById(@PathVariable @NotBlank String customerId) {
        return ResponseEntity.ok(customerService.getCustomerDtoById(customerId));
    }

    @GetMapping("/select/{customerId}")
    @Operation(summary = "Yetkili kullanıcının müşteri seçmesini sağlar")
    @ApiResponse(responseCode = "200", description = "Başarılı Response!")
    public ResponseEntity<CustomerSelectResponse> selectCustomerById(@PathVariable @NotBlank String customerId) {
        return ResponseEntity.ok(customerService.selectCustomerById(customerId));
    }

    @GetMapping("/all")
    @Operation(summary = "Oturumdaki yetkili kullanıcının veya path'de belirtilmiş kullanıcının müşterilerini döner")
    @ApiResponse(responseCode = "200", description = "Başarılı Response!")
    public ResponseEntity<List<CustomerDto>> getAllCustomersDtoByUserId(@RequestParam Optional<String> userId) {
        return ResponseEntity.ok(customerService.getAllCustomersDtoByUserId(userId));
    }

    @PostMapping("/create")
    @Operation(summary = "Yeni bir muşteri oluşturur")
    @ApiResponse(responseCode = "200", description = "Başarılı Response!")
    public ResponseEntity<CustomerDto> createCustomer(@Valid @RequestBody CreateCustomerRequest request) {
        return ResponseEntity.ok(customerService.createCustomer(request));
    }

}
