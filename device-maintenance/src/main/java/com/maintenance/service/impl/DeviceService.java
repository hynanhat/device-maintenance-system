package com.maintenance.service.impl;

import com.maintenance.dto.request.DeviceRequest;
import com.maintenance.dto.response.DeviceResponse;
import com.maintenance.dto.response.PageResponse;
import com.maintenance.entity.Device;
import com.maintenance.enums.DeviceStatus;
import com.maintenance.exception.BadRequestException;
import com.maintenance.exception.ResourceNotFoundException;
import com.maintenance.repository.DeviceRepository;
import com.maintenance.repository.IncidentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final IncidentRepository incidentRepository;

    public PageResponse<DeviceResponse> getDevices(String keyword, String status, String location,
                                                   int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        DeviceStatus deviceStatus = null;
        if (status != null && !status.isEmpty()) {
            try { deviceStatus = DeviceStatus.valueOf(status); } catch (Exception ignored) {}
        }
        Page<Device> devicePage = deviceRepository.searchDevices(keyword, deviceStatus, location, pageable);
        Page<DeviceResponse> responsePage = devicePage.map(this::toResponse);
        return PageResponse.of(responsePage);
    }

    public DeviceResponse getById(Long id) {
        return toResponse(findById(id));
    }

    public DeviceResponse getByCode(String code) {
        Device device = deviceRepository.findByDeviceCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Thiết bị không tìm thấy với mã: " + code));
        return toResponse(device);
    }

    @Transactional
    public DeviceResponse create(DeviceRequest request) {
        if (deviceRepository.existsByDeviceCode(request.getDeviceCode())) {
            throw new BadRequestException("Mã thiết bị đã tồn tại: " + request.getDeviceCode());
        }
        Device device = Device.builder()
                .deviceCode(request.getDeviceCode())
                .name(request.getName())
                .deviceType(request.getDeviceType())
                .manufacturer(request.getManufacturer())
                .modelNumber(request.getModelNumber())
                .serialNumber(request.getSerialNumber())
                .purchaseDate(request.getPurchaseDate())
                .warrantyExpiryDate(request.getWarrantyExpiryDate())
                .purchasePrice(request.getPurchasePrice())
                .status(request.getStatus() != null ? request.getStatus() : DeviceStatus.ACTIVE)
                .location(request.getLocation())
                .locationDetail(request.getLocationDetail())
                .description(request.getDescription())
                .maintenanceCycleDays(request.getMaintenanceCycleDays() != null ? request.getMaintenanceCycleDays() : 90)
                .build();
        return toResponse(deviceRepository.save(device));
    }

    @Transactional
    public DeviceResponse update(Long id, DeviceRequest request) {
        Device device = findById(id);
        if (!device.getDeviceCode().equals(request.getDeviceCode()) &&
                deviceRepository.existsByDeviceCode(request.getDeviceCode())) {
            throw new BadRequestException("Mã thiết bị đã tồn tại: " + request.getDeviceCode());
        }
        device.setDeviceCode(request.getDeviceCode());
        device.setName(request.getName());
        if (request.getDeviceType() != null) device.setDeviceType(request.getDeviceType());
        if (request.getManufacturer() != null) device.setManufacturer(request.getManufacturer());
        if (request.getModelNumber() != null) device.setModelNumber(request.getModelNumber());
        if (request.getSerialNumber() != null) device.setSerialNumber(request.getSerialNumber());
        if (request.getPurchaseDate() != null) device.setPurchaseDate(request.getPurchaseDate());
        if (request.getWarrantyExpiryDate() != null) device.setWarrantyExpiryDate(request.getWarrantyExpiryDate());
        if (request.getPurchasePrice() != null) device.setPurchasePrice(request.getPurchasePrice());
        if (request.getStatus() != null) device.setStatus(request.getStatus());
        if (request.getLocation() != null) device.setLocation(request.getLocation());
        if (request.getLocationDetail() != null) device.setLocationDetail(request.getLocationDetail());
        if (request.getDescription() != null) device.setDescription(request.getDescription());
        if (request.getMaintenanceCycleDays() != null) device.setMaintenanceCycleDays(request.getMaintenanceCycleDays());
        return toResponse(deviceRepository.save(device));
    }

    @Transactional
    public DeviceResponse updateStatus(Long id, DeviceStatus status) {
        Device device = findById(id);
        device.setStatus(status);
        return toResponse(deviceRepository.save(device));
    }
    // Chuyển sang Xóa Mềm (Soft Delete)
    @Transactional
    public void delete(Long id) {
        Device device = findById(id);
        device.setStatus(DeviceStatus.DISPOSED); // Cập nhật trạng thái thành Đã thanh lý
        deviceRepository.save(device);
    }

    @Transactional
    public DeviceResponse updateImage(Long id, String imageUrl) {
        Device device = findById(id);
        device.setImageUrl(imageUrl);
        return toResponse(deviceRepository.save(device));
    }

    public List<DeviceResponse> getAllActive() {
        return deviceRepository.findByStatus(DeviceStatus.ACTIVE)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    private Device findById(Long id) {
        return deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device", id));
    }

    public DeviceResponse toResponse(Device d) {
        long activeIncidents = incidentRepository.countActiveByDevice(d.getId());
        return DeviceResponse.builder()
                .id(d.getId())
                .deviceCode(d.getDeviceCode())
                .name(d.getName())
                .deviceType(d.getDeviceType())
                .manufacturer(d.getManufacturer())
                .modelNumber(d.getModelNumber())
                .serialNumber(d.getSerialNumber())
                .purchaseDate(d.getPurchaseDate())
                .warrantyExpiryDate(d.getWarrantyExpiryDate())
                .purchasePrice(d.getPurchasePrice())
                .status(d.getStatus())
                .location(d.getLocation())
                .locationDetail(d.getLocationDetail())
                .description(d.getDescription())
                .imageUrl(d.getImageUrl())
                .maintenanceCycleDays(d.getMaintenanceCycleDays())
                .createdAt(d.getCreatedAt())
                .updatedAt(d.getUpdatedAt())
                .activeIncidentCount(activeIncidents)
                .build();
    }
}