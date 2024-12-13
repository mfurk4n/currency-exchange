package com.finexchange.finexchange.controller;

import com.finexchange.finexchange.dto.request.RefreshTokenRequest;
import com.finexchange.finexchange.dto.request.UserLoginRequest;
import com.finexchange.finexchange.dto.request.UserRegisterRequest;
import com.finexchange.finexchange.dto.response.AuthResponse;
import com.finexchange.finexchange.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication Related APIs")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Mevcut kullanıcı girişi")
    @ApiResponse(responseCode = "200", description = "Successfully Request!")
    public ResponseEntity<AuthResponse> loginUser(@Valid @RequestBody UserLoginRequest userLoginRequest) {
        return ResponseEntity.ok(authService.loginUser(userLoginRequest));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh token yenilemesi")
    @ApiResponse(responseCode = "200", description = "Successfully Request!")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        AuthResponse authResponse = authService.refreshToken(refreshTokenRequest);
        if (authResponse.getJwtToken() == null) {
            return new ResponseEntity<>(authResponse, HttpStatus.UNAUTHORIZED);
        } else {
            return new ResponseEntity<>(authResponse, HttpStatus.OK);
        }

    }

}