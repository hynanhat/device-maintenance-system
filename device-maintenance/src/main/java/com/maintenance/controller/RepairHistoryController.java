package com.maintenance.controller;

import com.maintenance.dto.request.RepairHistoryRequest;
import com.maintenance.dto.response.*;
import com.maintenance.service.impl.RepairHistoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/repair-histories")
@RequiredArgsConstructor
public class RepairHistoryController {
    private final RepairHistoryService repairHistoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<RepairHistoryResponse>>> getAll(
            @RequestParam(required = false) Long deviceId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(repairHistoryService.getAll(deviceId, keyword, page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RepairHistoryResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(repairHistoryService.getById(id)));
    }

    @GetMapping("/device/{deviceId}/total-cost")
    public ResponseEntity<ApiResponse<Double>> getTotalCostByDevice(@PathVariable Long deviceId) {
        return ResponseEntity.ok(ApiResponse.success(repairHistoryService.getTotalCostByDevice(deviceId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RepairHistoryResponse>> create(@Valid @RequestBody RepairHistoryRequest request) {
        return ResponseEntity.ok(ApiResponse.success(repairHistoryService.create(request), "Ghi nhan lich su sua chua thanh cong"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RepairHistoryResponse>> update(@PathVariable Long id, @Valid @RequestBody RepairHistoryRequest request) {
        return ResponseEntity.ok(ApiResponse.success(repairHistoryService.update(id, request), "Cap nhat thanh cong"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        repairHistoryService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Xoa thanh cong"));
    }
}
