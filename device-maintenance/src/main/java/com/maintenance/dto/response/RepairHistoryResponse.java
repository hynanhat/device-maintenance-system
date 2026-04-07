package com.maintenance.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class RepairHistoryResponse {
    private Long id;
    private String repairCode;
    private String title;
    private String repairType;
    private String description;
    private LocalDateTime repairDate;
    private Double cost;
    private String partsReplaced;
    private String technicianNote;
    private String imageUrl;
    private Long deviceId;
    private String deviceName;
    private Long incidentId;
    private String incidentTitle;
    private Long maintenanceScheduleId;
    private String scheduleTitle;
    private Long performedById;
    private String performedByName;
    private LocalDateTime createdAt;
}
