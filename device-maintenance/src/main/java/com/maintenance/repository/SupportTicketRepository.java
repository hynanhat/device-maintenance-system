package com.maintenance.repository;

import com.maintenance.entity.SupportTicket;
import com.maintenance.enums.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {

    @Query("SELECT st FROM SupportTicket st WHERE " +
           "(:keyword IS NULL OR LOWER(st.subject) LIKE LOWER(CONCAT('%',:keyword,'%'))) AND " +
           "(:status IS NULL OR st.status = :status)")
    Page<SupportTicket> searchTickets(@Param("keyword") String keyword,
                                      @Param("status") TicketStatus status,
                                      Pageable pageable);

    Page<SupportTicket> findByCreatedById(Long userId, Pageable pageable);
    Page<SupportTicket> findByAssignedToId(Long userId, Pageable pageable);
    long countByStatus(TicketStatus status);
}
