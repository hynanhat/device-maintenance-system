package com.maintenance.service.impl;

import com.maintenance.dto.response.DashboardResponse;
import com.maintenance.entity.User;
import com.maintenance.enums.*;
import com.maintenance.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DeviceRepository deviceRepository;
    private final IncidentRepository incidentRepository;
    private final MaintenanceScheduleRepository scheduleRepository;
    private final RepairHistoryRepository repairHistoryRepository;
    private final UserRepository userRepository;
    private final UserService userService; // Đã bổ sung cái này

    public DashboardResponse getDashboard() {
        // Device counts
        long totalDevices = deviceRepository.count();
        long activeDevices = deviceRepository.countByStatus(DeviceStatus.ACTIVE);
        long underMaintenance = deviceRepository.countByStatus(DeviceStatus.UNDER_MAINTENANCE);
        long brokenDevices = deviceRepository.countByStatus(DeviceStatus.BROKEN);

        // Incident counts
        long totalIncidents = incidentRepository.count();
        long openIncidents = incidentRepository.countByStatus(IncidentStatus.REPORTED)
                + incidentRepository.countByStatus(IncidentStatus.IN_PROGRESS);
        long resolvedIncidents = incidentRepository.countByStatus(IncidentStatus.RESOLVED);

        // Schedule counts
        long totalSchedules = scheduleRepository.count();
        long scheduledMaintenance = scheduleRepository.countByStatus(MaintenanceStatus.SCHEDULED);
        long completedMaintenance = scheduleRepository.countByStatus(MaintenanceStatus.COMPLETED);
        long overdueSchedules = scheduleRepository.countByStatus(MaintenanceStatus.OVERDUE);

        // Costs
        Double repairCost = repairHistoryRepository.sumCostByDateRange(
                LocalDateTime.now().minusYears(10), LocalDateTime.now());
        Double maintenanceCost = scheduleRepository.sumCostByDateRange(
                LocalDateTime.now().minusYears(10), LocalDateTime.now());

        // User stats
        long totalUsers = userRepository.count();
        long technicians = userRepository.findAllTechnicians().size();

        // Charts
        Map<String, Long> incidentsByMonth = new LinkedHashMap<>();
        Map<String, Long> maintenanceByStatus = new LinkedHashMap<>();
        Map<String, Long> devicesByStatus = new LinkedHashMap<>();
        Map<String, Double> costByMonth = new LinkedHashMap<>();

        devicesByStatus.put("ACTIVE", activeDevices);
        devicesByStatus.put("UNDER_MAINTENANCE", underMaintenance);
        devicesByStatus.put("BROKEN", brokenDevices);

        maintenanceByStatus.put("SCHEDULED", scheduledMaintenance);
        maintenanceByStatus.put("COMPLETED", completedMaintenance);
        maintenanceByStatus.put("OVERDUE", overdueSchedules);

        for (int i = 5; i >= 0; i--) {
            LocalDateTime monthStart = LocalDateTime.now().minusMonths(i).withDayOfMonth(1).withHour(0).withMinute(0);
            LocalDateTime monthEnd = monthStart.plusMonths(1).minusSeconds(1);
            long incCount = incidentRepository.findByDateRange(monthStart, monthEnd).size();
            String month = monthStart.getMonth().name().substring(0, 3) + "/" + monthStart.getYear();
            incidentsByMonth.put(month, incCount);
        }

        for (int i = 5; i >= 0; i--) {
            LocalDateTime monthStart = LocalDateTime.now().minusMonths(i).withDayOfMonth(1).withHour(0).withMinute(0);
            LocalDateTime monthEnd = monthStart.plusMonths(1).minusSeconds(1);
            Double cost = repairHistoryRepository.sumCostByDateRange(monthStart, monthEnd);
            String month = monthStart.getMonth().name().substring(0, 3) + "/" + monthStart.getYear();
            costByMonth.put(month, cost != null ? cost : 0.0);
        }

        return DashboardResponse.builder()
                .totalDevices(totalDevices)
                .activeDevices(activeDevices)
                .underMaintenanceDevices(underMaintenance)
                .brokenDevices(brokenDevices)
                .totalIncidents(totalIncidents)
                .openIncidents(openIncidents)
                .resolvedIncidents(resolvedIncidents)
                .totalSchedules(totalSchedules)
                .scheduledMaintenance(scheduledMaintenance)
                .completedMaintenance(completedMaintenance)
                .overdueSchedules(overdueSchedules)
                .totalRepairCost(repairCost != null ? repairCost : 0.0)
                .totalMaintenanceCost(maintenanceCost != null ? maintenanceCost : 0.0)
                .totalUsers(totalUsers)
                .totalTechnicians(technicians)
                .incidentsByMonth(incidentsByMonth)
                .devicesByStatus(devicesByStatus)
                .costByMonth(costByMonth)
                .build();
    }

    // Logic mới vừa được thêm cho Kỹ thuật viên
    public Map<String, Object> getTechnicianDashboard() {
        User currentUser = userService.getCurrentUser();
        Long techId = currentUser.getId();

        long pendingIncidents = incidentRepository.findByAssignedTechnicianId(techId).stream()
                .filter(i -> i.getStatus() == IncidentStatus.IN_PROGRESS || i.getStatus() == IncidentStatus.REPORTED).count();
        long resolvedIncidents = incidentRepository.findByAssignedTechnicianId(techId).stream()
                .filter(i -> i.getStatus() == IncidentStatus.RESOLVED).count();

        long upcomingSchedules = scheduleRepository.findByAssignedTechnicianId(techId).stream()
                .filter(s -> s.getStatus() == MaintenanceStatus.SCHEDULED).count();
        long completedSchedules = scheduleRepository.findByAssignedTechnicianId(techId).stream()
                .filter(s -> s.getStatus() == MaintenanceStatus.COMPLETED).count();

        return Map.of(
                "pendingIncidents", pendingIncidents,
                "resolvedIncidents", resolvedIncidents,
                "upcomingSchedules", upcomingSchedules,
                "completedSchedules", completedSchedules
        );
    }
}