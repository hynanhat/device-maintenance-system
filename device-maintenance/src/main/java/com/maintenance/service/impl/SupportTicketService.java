package com.maintenance.service.impl;

import com.maintenance.dto.request.SupportTicketRequest;
import com.maintenance.dto.response.PageResponse;
import com.maintenance.dto.response.SupportTicketResponse;
import com.maintenance.entity.*;
import com.maintenance.enums.TicketStatus;
import com.maintenance.exception.ResourceNotFoundException;
import com.maintenance.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class SupportTicketService {

    private final SupportTicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public PageResponse<SupportTicketResponse> getAll(String keyword, String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        TicketStatus ts = null;
        if (status != null && !status.isEmpty()) {
            try { ts = TicketStatus.valueOf(status); } catch (Exception ignored) {}
        }
        return PageResponse.of(ticketRepository.searchTickets(keyword, ts, pageable).map(this::toResponse));
    }

    public SupportTicketResponse getById(Long id) {
        return toResponse(findById(id));
    }

    @Transactional
    public SupportTicketResponse create(SupportTicketRequest request) {
        User currentUser = userService.getCurrentUser();
        String code = "TK-" + DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDateTime.now())
                      + "-" + String.format("%04d", new Random().nextInt(9999));

        SupportTicket ticket = SupportTicket.builder()
                .ticketCode(code)
                .subject(request.getSubject())
                .content(request.getContent())
                .priority(request.getPriority())
                .status(TicketStatus.OPEN)
                .createdBy(currentUser)
                .build();
        return toResponse(ticketRepository.save(ticket));
    }

    @Transactional
    public SupportTicketResponse respond(Long id, SupportTicketRequest request) {
        SupportTicket ticket = findById(id);
        if (request.getAdminResponse() != null) ticket.setAdminResponse(request.getAdminResponse());
        if (request.getStatus() != null) {
            ticket.setStatus(request.getStatus());
            if (request.getStatus() == TicketStatus.RESOLVED || request.getStatus() == TicketStatus.CLOSED) {
                ticket.setResolvedAt(LocalDateTime.now());
            }
        }
        if (request.getAssignedToId() != null) {
            User assignee = userRepository.findById(request.getAssignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", request.getAssignedToId()));
            ticket.setAssignedTo(assignee);
        }
        return toResponse(ticketRepository.save(ticket));
    }

    @Transactional
    public void delete(Long id) {
        if (!ticketRepository.existsById(id)) throw new ResourceNotFoundException("SupportTicket", id);
        ticketRepository.deleteById(id);
    }

    public PageResponse<SupportTicketResponse> getMyTickets(int page, int size) {
        User currentUser = userService.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return PageResponse.of(ticketRepository.findByCreatedById(currentUser.getId(), pageable).map(this::toResponse));
    }

    private SupportTicket findById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SupportTicket", id));
    }

    private SupportTicketResponse toResponse(SupportTicket t) {
        return SupportTicketResponse.builder()
                .id(t.getId())
                .ticketCode(t.getTicketCode())
                .subject(t.getSubject())
                .content(t.getContent())
                .priority(t.getPriority())
                .status(t.getStatus())
                .adminResponse(t.getAdminResponse())
                .resolvedAt(t.getResolvedAt())
                .createdById(t.getCreatedBy() != null ? t.getCreatedBy().getId() : null)
                .createdByName(t.getCreatedBy() != null ? t.getCreatedBy().getFullName() : null)
                .assignedToId(t.getAssignedTo() != null ? t.getAssignedTo().getId() : null)
                .assignedToName(t.getAssignedTo() != null ? t.getAssignedTo().getFullName() : null)
                .createdAt(t.getCreatedAt())
                .updatedAt(t.getUpdatedAt())
                .build();
    }
}
