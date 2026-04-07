package com.maintenance.dto.response;

import com.maintenance.enums.IncidentStatus;
import com.maintenance.enums.SeverityLevel;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class IncidentResponse {
    private Long id;
    private String incidentCode;
    private String title;
    private String description;
    private SeverityLevel severity;
    private IncidentStatus status;
    private LocalDateTime occurredAt;
    private LocalDateTime resolvedAt;
    private String resolutionNote;
    private String imageUrl;
    private Long deviceId;
    private String deviceName;
    private String deviceCode;
    private Long reportedById;
    private String reportedByName;
    private Long assignedTechnicianId;
    private String assignedTechnicianName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
