package com.maintenance.service.impl;

import com.maintenance.dto.request.RepairHistoryRequest;
import com.maintenance.dto.response.PageResponse;
import com.maintenance.dto.response.RepairHistoryResponse;
import com.maintenance.entity.*;
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
public class RepairHistoryService {

    private final RepairHistoryRepository repairHistoryRepository;
    private final DeviceRepository deviceRepository;
    private final IncidentRepository incidentRepository;
    private final MaintenanceScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public PageResponse<RepairHistoryResponse> getAll(Long deviceId, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("repairDate").descending());
        Page<RepairHistory> result = repairHistoryRepository.searchRepairHistories(deviceId, keyword, pageable);
        return PageResponse.of(result.map(this::toResponse));
    }

    public RepairHistoryResponse getById(Long id) {
        return toResponse(findById(id));
    }

    @Transactional
    public RepairHistoryResponse create(RepairHistoryRequest request) {
        Device device = deviceRepository.findById(request.getDeviceId())
                .orElseThrow(() -> new ResourceNotFoundException("Device", request.getDeviceId()));

        User currentUser = userService.getCurrentUser();
        User performer = currentUser;
        if (request.getPerformedById() != null) {
            performer = userRepository.findById(request.getPerformedById())
                    .orElseThrow(() -> new ResourceNotFoundException("User", request.getPerformedById()));
        }

        Incident incident = null;
        if (request.getIncidentId() != null) {
            incident = incidentRepository.findById(request.getIncidentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Incident", request.getIncidentId()));
        }

        MaintenanceSchedule schedule = null;
        if (request.getMaintenanceScheduleId() != null) {
            schedule = scheduleRepository.findById(request.getMaintenanceScheduleId())
                    .orElseThrow(() -> new ResourceNotFoundException("MaintenanceSchedule", request.getMaintenanceScheduleId()));
        }

        String code = "RH-" + DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDateTime.now())
                      + "-" + String.format("%04d", new Random().nextInt(9999));

        RepairHistory history = RepairHistory.builder()
                .repairCode(code)
                .title(request.getTitle())
                .repairType(request.getRepairType())
                .description(request.getDescription())
                .repairDate(request.getRepairDate())
                .cost(request.getCost())
                .partsReplaced(request.getPartsReplaced())
                .technicianNote(request.getTechnicianNote())
                .device(device)
                .incident(incident)
                .maintenanceSchedule(schedule)
                .performedBy(performer)
                .build();

        return toResponse(repairHistoryRepository.save(history));
    }

    @Transactional
    public RepairHistoryResponse update(Long id, RepairHistoryRequest request) {
        RepairHistory history = findById(id);
        if (request.getTitle() != null) history.setTitle(request.getTitle());
        if (request.getRepairType() != null) history.setRepairType(request.getRepairType());
        if (request.getDescription() != null) history.setDescription(request.getDescription());
        if (request.getRepairDate() != null) history.setRepairDate(request.getRepairDate());
        if (request.getCost() != null) history.setCost(request.getCost());
        if (request.getPartsReplaced() != null) history.setPartsReplaced(request.getPartsReplaced());
        if (request.getTechnicianNote() != null) history.setTechnicianNote(request.getTechnicianNote());
        return toResponse(repairHistoryRepository.save(history));
    }

    @Transactional
    public void delete(Long id) {
        if (!repairHistoryRepository.existsById(id)) throw new ResourceNotFoundException("RepairHistory", id);
        repairHistoryRepository.deleteById(id);
    }

    public Double getTotalCostByDevice(Long deviceId) {
        Double cost = repairHistoryRepository.sumCostByDevice(deviceId);
        return cost != null ? cost : 0.0;
    }

    private RepairHistory findById(Long id) {
        return repairHistoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RepairHistory", id));
    }

    public RepairHistoryResponse toResponse(RepairHistory rh) {
        return RepairHistoryResponse.builder()
                .id(rh.getId())
                .repairCode(rh.getRepairCode())
                .title(rh.getTitle())
                .repairType(rh.getRepairType())
                .description(rh.getDescription())
                .repairDate(rh.getRepairDate())
                .cost(rh.getCost())
                .partsReplaced(rh.getPartsReplaced())
                .technicianNote(rh.getTechnicianNote())
                .imageUrl(rh.getImageUrl())
                .deviceId(rh.getDevice() != null ? rh.getDevice().getId() : null)
                .deviceName(rh.getDevice() != null ? rh.getDevice().getName() : null)
                .incidentId(rh.getIncident() != null ? rh.getIncident().getId() : null)
                .incidentTitle(rh.getIncident() != null ? rh.getIncident().getTitle() : null)
                .maintenanceScheduleId(rh.getMaintenanceSchedule() != null ? rh.getMaintenanceSchedule().getId() : null)
                .scheduleTitle(rh.getMaintenanceSchedule() != null ? rh.getMaintenanceSchedule().getTitle() : null)
                .performedById(rh.getPerformedBy() != null ? rh.getPerformedBy().getId() : null)
                .performedByName(rh.getPerformedBy() != null ? rh.getPerformedBy().getFullName() : null)
                .createdAt(rh.getCreatedAt())
                .build();
    }
}
