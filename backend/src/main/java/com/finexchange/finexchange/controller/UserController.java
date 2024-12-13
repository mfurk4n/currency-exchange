package com.finexchange.finexchange.controller;

import com.finexchange.finexchange.dto.UserDto;
import com.finexchange.finexchange.dto.request.UserPasswordUpdateRequest;
import com.finexchange.finexchange.dto.request.UserRegisterRequest;
import com.finexchange.finexchange.model.User;
import com.finexchange.finexchange.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "User Related APIs")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/create-user")
    @Operation(summary = "Yeni kullanıcı oluşturur")
    @ApiResponse(responseCode = "200", description = "Başarılı Ressponse")
    public ResponseEntity<User> createUser(@Valid @RequestBody UserRegisterRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/create-admin/{userId}")
    @Operation(summary = "Kullanıcıyı admin olarak oluşturur")
    @ApiResponse(responseCode = "200", description = "Başarılı Ressponse")
    public ResponseEntity<User> createAdmin(@PathVariable String userId) {
        return ResponseEntity.ok(userService.createAdmin(userId));
    }

    @GetMapping("/all")
    @Operation(summary = "Tüm kullanıcıları döner")
    @ApiResponse(responseCode = "200", description = "Başarılı Ressponse")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/{userId}")
    @Operation(summary = "Şifre güncelleme işlemi")
    @ApiResponse(responseCode = "200", description = "Başarılı Ressponse")
    public ResponseEntity<String> updateUserPassword(@PathVariable String userId, @Valid @RequestBody UserPasswordUpdateRequest updateRequest) {
        return ResponseEntity.ok(userService.updateUserPassword(userId, updateRequest));
    }

    @GetMapping("/get-user")
    @Operation(summary = "Kullanıcı bilgisini döner")
    @ApiResponse(responseCode = "200", description = "Başarılı Ressponse")
    public ResponseEntity<UserDto> getUserDtoById() {
        return ResponseEntity.ok(userService.getUserDtoById());
    }
}
