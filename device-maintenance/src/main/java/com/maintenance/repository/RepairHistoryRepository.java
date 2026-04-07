package com.maintenance.repository;

import com.maintenance.entity.RepairHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RepairHistoryRepository extends JpaRepository<RepairHistory, Long> {
    List<RepairHistory> findByDeviceId(Long deviceId);
    List<RepairHistory> findByIncidentId(Long incidentId);
    List<RepairHistory> findByMaintenanceScheduleId(Long scheduleId);

    @Query("SELECT rh FROM RepairHistory rh WHERE " +
           "(:deviceId IS NULL OR rh.device.id = :deviceId) AND " +
           "(:keyword IS NULL OR LOWER(rh.title) LIKE LOWER(CONCAT('%',:keyword,'%')))")
    Page<RepairHistory> searchRepairHistories(@Param("deviceId") Long deviceId,
                                              @Param("keyword") String keyword,
                                              Pageable pageable);

    @Query("SELECT SUM(rh.cost) FROM RepairHistory rh WHERE rh.device.id = :deviceId")
    Double sumCostByDevice(@Param("deviceId") Long deviceId);

    @Query("SELECT SUM(rh.cost) FROM RepairHistory rh WHERE rh.repairDate BETWEEN :from AND :to")
    Double sumCostByDateRange(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT rh FROM RepairHistory rh WHERE rh.repairDate BETWEEN :from AND :to ORDER BY rh.repairDate DESC")
    List<RepairHistory> findByDateRange(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
