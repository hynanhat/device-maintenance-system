package com.maintenance.controller;

import com.maintenance.dto.request.MaintenanceScheduleRequest;
import com.maintenance.dto.response.*;
import com.maintenance.service.impl.MaintenanceScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/maintenance-schedules")
@RequiredArgsConstructor
public class MaintenanceScheduleController {
    private final MaintenanceScheduleService scheduleService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<MaintenanceScheduleResponse>>> getAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long deviceId,
            @RequestParam(required = false) Long technicianId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "scheduledDate") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        return ResponseEntity.ok(ApiResponse.success(scheduleService.getAll(keyword, status, deviceId, technicianId, page, size, sortBy, direction)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MaintenanceScheduleResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(scheduleService.getById(id)));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<ApiResponse<List<MaintenanceScheduleResponse>>> getUpcoming(
            @RequestParam(defaultValue = "7") int days) {
        return ResponseEntity.ok(ApiResponse.success(scheduleService.getUpcoming(days)));
    }

    @GetMapping("/overdue")
    public ResponseEntity<ApiResponse<List<MaintenanceScheduleResponse>>> getOverdue() {
        return ResponseEntity.ok(ApiResponse.success(scheduleService.getOverdue()));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<MaintenanceScheduleResponse>> create(@Valid @RequestBody MaintenanceScheduleRequest request) {
        return ResponseEntity.ok(ApiResponse.success(scheduleService.create(request), "Tao lich bao tri thanh cong"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','TECHNICIAN')")
    public ResponseEntity<ApiResponse<MaintenanceScheduleResponse>> update(@PathVariable Long id,
                                                                           @Valid @RequestBody MaintenanceScheduleRequest request) {
        return ResponseEntity.ok(ApiResponse.success(scheduleService.update(id, request), "Cap nhat thanh cong"));
    }

    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','TECHNICIAN')")
    public ResponseEntity<ApiResponse<MaintenanceScheduleResponse>> complete(@PathVariable Long id,
                                                                              @RequestParam(required = false) Double cost,
                                                                              @RequestParam(required = false) String notes) {
        return ResponseEntity.ok(ApiResponse.success(scheduleService.complete(id, cost, notes), "Hoan thanh bao tri"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        scheduleService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Xoa lich bao tri thanh cong"));
    }
}
