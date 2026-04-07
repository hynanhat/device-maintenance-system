package com.maintenance.controller;

import com.maintenance.dto.request.IncidentRequest;
import com.maintenance.dto.response.*;
import com.maintenance.service.impl.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@RestController
@RequestMapping("/incidents")
@RequiredArgsConstructor
public class IncidentController {
    private final IncidentService incidentService;
    private final FileStorageService fileStorageService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<IncidentResponse>>> getAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) Long deviceId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        return ResponseEntity.ok(ApiResponse.success(incidentService.getAll(keyword, status, severity, deviceId, page, size, sortBy, direction)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<IncidentResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(incidentService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<IncidentResponse>> create(@Valid @RequestBody IncidentRequest request) {
        return ResponseEntity.ok(ApiResponse.success(incidentService.create(request), "Ghi nhan su co thanh cong"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<IncidentResponse>> update(@PathVariable Long id, @Valid @RequestBody IncidentRequest request) {
        return ResponseEntity.ok(ApiResponse.success(incidentService.update(id, request), "Cap nhat su co thanh cong"));
    }

    @PatchMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<IncidentResponse>> assign(@PathVariable Long id, @RequestParam Long technicianId) {
        return ResponseEntity.ok(ApiResponse.success(incidentService.assignTechnician(id, technicianId), "Phan cong thanh cong"));
    }

    @PatchMapping("/{id}/resolve")
    public ResponseEntity<ApiResponse<IncidentResponse>> resolve(@PathVariable Long id, @RequestParam(required = false) String resolutionNote) {
        return ResponseEntity.ok(ApiResponse.success(incidentService.resolve(id, resolutionNote), "Da giai quyet su co"));
    }

    @PostMapping("/{id}/image")
    public ResponseEntity<ApiResponse<IncidentResponse>> uploadImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        Map<String, String> uploaded = fileStorageService.uploadFile(file, "INCIDENT", id);
        IncidentResponse updated = incidentService.updateImage(id, uploaded.get("fileUrl"));
        return ResponseEntity.ok(ApiResponse.success(updated, "Upload anh thanh cong"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        incidentService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Xoa su co thanh cong"));
    }
}
