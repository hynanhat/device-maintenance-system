package com.maintenance.controller;

import com.maintenance.dto.request.SupportTicketRequest;
import com.maintenance.dto.response.*;
import com.maintenance.service.impl.SupportTicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/support-tickets")
@RequiredArgsConstructor
public class SupportTicketController {
    private final SupportTicketService ticketService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<PageResponse<SupportTicketResponse>>> getAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(ticketService.getAll(keyword, status, page, size)));
    }

    @GetMapping("/my-tickets")
    public ResponseEntity<ApiResponse<PageResponse<SupportTicketResponse>>> getMyTickets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(ticketService.getMyTickets(page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SupportTicketResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(ticketService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SupportTicketResponse>> create(@Valid @RequestBody SupportTicketRequest request) {
        return ResponseEntity.ok(ApiResponse.success(ticketService.create(request), "Gui yeu cau ho tro thanh cong"));
    }

    @PutMapping("/{id}/respond")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<SupportTicketResponse>> respond(@PathVariable Long id,
                                                                       @RequestBody SupportTicketRequest request) {
        return ResponseEntity.ok(ApiResponse.success(ticketService.respond(id, request), "Phan hoi thanh cong"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        ticketService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Xoa thanh cong"));
    }
}
