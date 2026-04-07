package com.maintenance.dto.response;

import com.maintenance.enums.SeverityLevel;
import com.maintenance.enums.TicketStatus;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class SupportTicketResponse {
    private Long id;
    private String ticketCode;
    private String subject;
    private String content;
    private SeverityLevel priority;
    private TicketStatus status;
    private String adminResponse;
    private LocalDateTime resolvedAt;
    private Long createdById;
    private String createdByName;
    private Long assignedToId;
    private String assignedToName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
