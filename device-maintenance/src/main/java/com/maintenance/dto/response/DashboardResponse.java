package com.maintenance.dto.response;

import lombok.*;
import java.util.Map;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class DashboardResponse {
    // Device stats
    private Long totalDevices;
    private Long activeDevices;
    private Long underMaintenanceDevices;
    private Long brokenDevices;
    // Incident stats
    private Long totalIncidents;
    private Long openIncidents;
    private Long resolvedIncidents;
    // Maintenance stats
    private Long totalSchedules;
    private Long scheduledMaintenance;
    private Long completedMaintenance;
    private Long overdueSchedules;
    // Cost stats
    private Double totalRepairCost;
    private Double totalMaintenanceCost;
    // User stats
    private Long totalUsers;
    private Long totalTechnicians;
    // Charts data
    private Map<String, Long> incidentsByMonth;
    private Map<String, Long> maintenanceByStatus;
    private Map<String, Long> devicesByStatus;
    private Map<String, Double> costByMonth;
}
