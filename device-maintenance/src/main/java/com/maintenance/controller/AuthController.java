package com.maintenance.controller;

import com.maintenance.dto.request.*;
import com.maintenance.dto.response.*;
import com.maintenance.service.impl.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "1. Authentication")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Dang nhap - Tra ve access token va refresh token")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authService.login(request), "Dang nhap thanh cong"));
    }

    @PostMapping("/register")
    @Operation(summary = "Dang ky tai khoan moi")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authService.register(request), "Dang ky thanh cong"));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Lam moi access token bang refresh token")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@RequestParam String refreshToken) {
        return ResponseEntity.ok(ApiResponse.success(authService.refreshToken(refreshToken), "Token da duoc lam moi"));
    }
}
