package com.maintenance.repository;

import com.maintenance.entity.MediaFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MediaFileRepository extends JpaRepository<MediaFile, Long> {
    List<MediaFile> findByEntityTypeAndEntityId(String entityType, Long entityId);
    List<MediaFile> findByUploadedById(Long userId);
}
