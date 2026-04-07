package com.maintenance.repository;

import com.maintenance.entity.Device;
import com.maintenance.enums.DeviceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    Optional<Device> findByDeviceCode(String deviceCode);
    boolean existsByDeviceCode(String deviceCode);
    List<Device> findByStatus(DeviceStatus status);

    @Query("SELECT d FROM Device d WHERE " +
           "(:keyword IS NULL OR LOWER(d.name) LIKE LOWER(CONCAT('%',:keyword,'%')) OR " +
           "LOWER(d.deviceCode) LIKE LOWER(CONCAT('%',:keyword,'%')) OR " +
           "LOWER(d.deviceType) LIKE LOWER(CONCAT('%',:keyword,'%'))) AND " +
           "(:status IS NULL OR d.status = :status) AND " +
           "(:location IS NULL OR LOWER(d.location) LIKE LOWER(CONCAT('%',:location,'%')))")
    Page<Device> searchDevices(@Param("keyword") String keyword,
                               @Param("status") DeviceStatus status,
                               @Param("location") String location,
                               Pageable pageable);

    @Query("SELECT COUNT(d) FROM Device d WHERE d.status = :status")
    long countByStatus(@Param("status") DeviceStatus status);

    @Query("SELECT d FROM Device d WHERE d.maintenanceCycleDays IS NOT NULL ORDER BY d.name")
    List<Device> findAllWithMaintenanceCycle();
}
