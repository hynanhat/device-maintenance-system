package com.maintenance.repository;

import com.maintenance.entity.Incident;
import com.maintenance.enums.IncidentStatus;
import com.maintenance.enums.SeverityLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {

    @Query("SELECT i FROM Incident i WHERE " +
           "(:keyword IS NULL OR LOWER(i.title) LIKE LOWER(CONCAT('%',:keyword,'%'))) AND " +
           "(:status IS NULL OR i.status = :status) AND " +
           "(:severity IS NULL OR i.severity = :severity) AND " +
           "(:deviceId IS NULL OR i.device.id = :deviceId)")
    Page<Incident> searchIncidents(@Param("keyword") String keyword,
                                   @Param("status") IncidentStatus status,
                                   @Param("severity") SeverityLevel severity,
                                   @Param("deviceId") Long deviceId,
                                   Pageable pageable);

    List<Incident> findByDeviceId(Long deviceId);
    List<Incident> findByAssignedTechnicianId(Long technicianId);
    List<Incident> findByStatus(IncidentStatus status);

    @Query("SELECT COUNT(i) FROM Incident i WHERE i.status = :status")
    long countByStatus(@Param("status") IncidentStatus status);

    @Query("SELECT COUNT(i) FROM Incident i WHERE i.device.id = :deviceId AND i.status != 'CLOSED'")
    long countActiveByDevice(@Param("deviceId") Long deviceId);

    @Query("SELECT i FROM Incident i WHERE i.createdAt BETWEEN :from AND :to")
    List<Incident> findByDateRange(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
