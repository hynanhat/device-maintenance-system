package com.maintenance.controller;

import com.maintenance.dto.request.DeviceRequest;
import com.maintenance.dto.response.*;
import com.maintenance.enums.DeviceStatus;
import com.maintenance.service.impl.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;

@RestController
@RequestMapping("/devices")
@RequiredArgsConstructor
public class DeviceController {
    private final DeviceService deviceService;
    private final FileStorageService fileStorageService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<DeviceResponse>>> getDevices(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String location,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        return ResponseEntity.ok(ApiResponse.success(deviceService.getDevices(keyword, status, location, page, size, sortBy, direction)));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<DeviceResponse>>> getActiveDevices() {
        return ResponseEntity.ok(ApiResponse.success(deviceService.getAllActive()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DeviceResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(deviceService.getById(id)));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<DeviceResponse>> getByCode(@PathVariable String code) {
        return ResponseEntity.ok(ApiResponse.success(deviceService.getByCode(code)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<DeviceResponse>> create(@Valid @RequestBody DeviceRequest request) {
        return ResponseEntity.ok(ApiResponse.success(deviceService.create(request), "Them thiet bi thanh cong"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<DeviceResponse>> update(@PathVariable Long id, @Valid @RequestBody DeviceRequest request) {
        return ResponseEntity.ok(ApiResponse.success(deviceService.update(id, request), "Cap nhat thanh cong"));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','TECHNICIAN')")
    public ResponseEntity<ApiResponse<DeviceResponse>> updateStatus(@PathVariable Long id, @RequestParam DeviceStatus status) {
        return ResponseEntity.ok(ApiResponse.success(deviceService.updateStatus(id, status), "Cap nhat trang thai thanh cong"));
    }

    @PostMapping("/{id}/image")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<DeviceResponse>> uploadImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        Map<String, String> uploaded = fileStorageService.uploadFile(file, "DEVICE", id);
        DeviceResponse updated = deviceService.updateImage(id, uploaded.get("fileUrl"));
        return ResponseEntity.ok(ApiResponse.success(updated, "Upload anh thanh cong"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        deviceService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Xoa thiet bi thanh cong"));
    }
}
