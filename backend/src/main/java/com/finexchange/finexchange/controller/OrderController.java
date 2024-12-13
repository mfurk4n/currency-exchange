package com.finexchange.finexchange.controller;

import com.finexchange.finexchange.dto.response.AllOrdersResponse;
import com.finexchange.finexchange.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/order")
@Tag(name = "Emir Okuma İlişkili API'ler")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping("/{customerId}")
    @Operation(summary = "Müşterinin tüm emirlerini bekleyen ve iptal edilen olarak ayırıp çeker")
    @ApiResponse(responseCode = "200", description = "Başarılı Response!")
    public ResponseEntity<AllOrdersResponse> getAllOrderResponseAsPart(@PathVariable String customerId) {
        return ResponseEntity.ok(orderService.getAllOrderResponseAsPart(customerId));
    }

    @GetMapping("/cancel/{orderId}")
    @Operation(summary = "Limit veya stop emri iptal eder")
    @ApiResponse(responseCode = "200", description = "Başarılı Response!")
    public ResponseEntity<Void> cancelOrder(@PathVariable String orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok().build();
    }
}
