package com.maintenance.service.impl;

import com.maintenance.dto.request.IncidentRequest;
import com.maintenance.dto.response.IncidentResponse;
import com.maintenance.dto.response.PageResponse;
import com.maintenance.entity.*;
import com.maintenance.enums.DeviceStatus;
import com.maintenance.enums.IncidentStatus;
import com.maintenance.enums.SeverityLevel;
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
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public PageResponse<IncidentResponse> getAll(String keyword, String status, String severity,
                                                  Long deviceId, int page, int size, String sortBy, String dir) {
        Sort sort = dir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        IncidentStatus incidentStatus = parseEnum(IncidentStatus.class, status);
        SeverityLevel severityLevel = parseEnum(SeverityLevel.class, severity);
        Page<Incident> incidents = incidentRepository.searchIncidents(keyword, incidentStatus, severityLevel, deviceId, pageable);
        return PageResponse.of(incidents.map(this::toResponse));
    }

    public IncidentResponse getById(Long id) {
        return toResponse(findById(id));
    }

    @Transactional
    public IncidentResponse create(IncidentRequest request) {
        Device device = deviceRepository.findById(request.getDeviceId())
                .orElseThrow(() -> new ResourceNotFoundException("Device", request.getDeviceId()));

        User currentUser = userService.getCurrentUser();
        User technician = null;
        if (request.getAssignedTechnicianId() != null) {
            technician = userRepository.findById(request.getAssignedTechnicianId())
                    .orElseThrow(() -> new ResourceNotFoundException("Technician", request.getAssignedTechnicianId()));
        }

        String code = "INC-" + DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDateTime.now())
                      + "-" + String.format("%04d", new Random().nextInt(9999));

        Incident incident = Incident.builder()
                .incidentCode(code)
                .title(request.getTitle())
                .description(request.getDescription())
                .severity(request.getSeverity())
                .status(IncidentStatus.REPORTED)
                .occurredAt(request.getOccurredAt() != null ? request.getOccurredAt() : LocalDateTime.now())
                .device(device)
                .reportedBy(currentUser)
                .assignedTechnician(technician)
                .build();

        // Cập nhật trạng thái thiết bị nếu sự cố nghiêm trọng
        if (request.getSeverity() == SeverityLevel.HIGH || request.getSeverity() == SeverityLevel.CRITICAL) {
            device.setStatus(DeviceStatus.BROKEN);
            deviceRepository.save(device);
        }

        return toResponse(incidentRepository.save(incident));
    }

    @Transactional
    public IncidentResponse update(Long id, IncidentRequest request) {
        Incident incident = findById(id);

        incident.setTitle(request.getTitle());
        if (request.getDescription() != null) incident.setDescription(request.getDescription());
        if (request.getSeverity() != null) incident.setSeverity(request.getSeverity());
        if (request.getResolutionNote() != null) incident.setResolutionNote(request.getResolutionNote());

        if (request.getAssignedTechnicianId() != null) {
            User tech = userRepository.findById(request.getAssignedTechnicianId())
                    .orElseThrow(() -> new ResourceNotFoundException("Technician", request.getAssignedTechnicianId()));
            incident.setAssignedTechnician(tech);
        }

        if (request.getStatus() != null) {
            incident.setStatus(request.getStatus());
            if (request.getStatus() == IncidentStatus.RESOLVED || request.getStatus() == IncidentStatus.CLOSED) {
                incident.setResolvedAt(LocalDateTime.now());
                // Khôi phục trạng thái thiết bị
                Device device = incident.getDevice();
                if (device.getStatus() == DeviceStatus.BROKEN) {
                    device.setStatus(DeviceStatus.ACTIVE);
                    deviceRepository.save(device);
                }
            }
        }

        return toResponse(incidentRepository.save(incident));
    }

    @Transactional
    public IncidentResponse assignTechnician(Long id, Long technicianId) {
        Incident incident = findById(id);
        User tech = userRepository.findById(technicianId)
                .orElseThrow(() -> new ResourceNotFoundException("Technician", technicianId));
        incident.setAssignedTechnician(tech);
        incident.setStatus(IncidentStatus.IN_PROGRESS);
        return toResponse(incidentRepository.save(incident));
    }

    @Transactional
    public IncidentResponse resolve(Long id, String resolutionNote) {
        Incident incident = findById(id);
        incident.setStatus(IncidentStatus.RESOLVED);
        incident.setResolvedAt(LocalDateTime.now());
        incident.setResolutionNote(resolutionNote);
        Device device = incident.getDevice();
        if (device.getStatus() == DeviceStatus.BROKEN) {
            device.setStatus(DeviceStatus.ACTIVE);
            deviceRepository.save(device);
        }
        return toResponse(incidentRepository.save(incident));
    }

    @Transactional
    public void delete(Long id) {
        if (!incidentRepository.existsById(id)) throw new ResourceNotFoundException("Incident", id);
        incidentRepository.deleteById(id);
    }

    @Transactional
    public IncidentResponse updateImage(Long id, String imageUrl) {
        Incident incident = findById(id);
        incident.setImageUrl(imageUrl);
        return toResponse(incidentRepository.save(incident));
    }

    private Incident findById(Long id) {
        return incidentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incident", id));
    }

    private <T extends Enum<T>> T parseEnum(Class<T> enumClass, String value) {
        if (value == null || value.isEmpty()) return null;
        try { return Enum.valueOf(enumClass, value); } catch (Exception e) { return null; }
    }

    public IncidentResponse toResponse(Incident i) {
        return IncidentResponse.builder()
                .id(i.getId())
                .incidentCode(i.getIncidentCode())
                .title(i.getTitle())
                .description(i.getDescription())
                .severity(i.getSeverity())
                .status(i.getStatus())
                .occurredAt(i.getOccurredAt())
                .resolvedAt(i.getResolvedAt())
                .resolutionNote(i.getResolutionNote())
                .imageUrl(i.getImageUrl())
                .deviceId(i.getDevice() != null ? i.getDevice().getId() : null)
                .deviceName(i.getDevice() != null ? i.getDevice().getName() : null)
                .deviceCode(i.getDevice() != null ? i.getDevice().getDeviceCode() : null)
                .reportedById(i.getReportedBy() != null ? i.getReportedBy().getId() : null)
                .reportedByName(i.getReportedBy() != null ? i.getReportedBy().getFullName() : null)
                .assignedTechnicianId(i.getAssignedTechnician() != null ? i.getAssignedTechnician().getId() : null)
                .assignedTechnicianName(i.getAssignedTechnician() != null ? i.getAssignedTechnician().getFullName() : null)
                .createdAt(i.getCreatedAt())
                .updatedAt(i.getUpdatedAt())
                .build();
    }
}
