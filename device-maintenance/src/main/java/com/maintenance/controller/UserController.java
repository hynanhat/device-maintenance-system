package com.maintenance.controller;

import com.maintenance.dto.request.*;
import com.maintenance.dto.response.*;
import com.maintenance.service.impl.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final FileStorageService fileStorageService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getProfile() {
        return ResponseEntity.ok(ApiResponse.success(userService.getProfile()));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(@RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(ApiResponse.success(userService.updateProfile(request), "Cap nhat thanh cong"));
    }

    @PostMapping("/me/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(request);
        return ResponseEntity.ok(ApiResponse.success(null, "Doi mat khau thanh cong"));
    }

    @PostMapping("/me/avatar")
    public ResponseEntity<ApiResponse<UserResponse>> uploadAvatar(@RequestParam("file") MultipartFile file) {
        Map<String, String> uploaded = fileStorageService.uploadFile(file, "AVATAR", null);
        UserResponse updated = userService.updateAvatar(userService.getCurrentUser().getId(), uploaded.get("fileUrl"));
        return ResponseEntity.ok(ApiResponse.success(updated, "Upload avatar thanh cong"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.success(userService.getAllUsers()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(userService.getUserById(id)));
    }

    @PostMapping("/admin/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(ApiResponse.success(userService.createUserByAdmin(request), "Tao tai khoan thanh cong"));
    }

    @PatchMapping("/{id}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> toggleStatus(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(userService.toggleUserStatus(id), "Cap nhat trang thai thanh cong"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Xoa nguoi dung thanh cong"));
    }

    @GetMapping("/technicians")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getTechnicians() {
        return ResponseEntity.ok(ApiResponse.success(userService.getTechnicians()));
    }
}
