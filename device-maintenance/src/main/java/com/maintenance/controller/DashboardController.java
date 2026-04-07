package com.maintenance.controller;

import com.maintenance.dto.response.*;
import com.maintenance.service.impl.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getDashboard()));
    }

    // API mới vừa được thêm cho Kỹ thuật viên
    @GetMapping("/technician")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTechnicianDashboard() {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getTechnicianDashboard(), "Lấy số liệu Kỹ thuật viên thành công"));
    }
}