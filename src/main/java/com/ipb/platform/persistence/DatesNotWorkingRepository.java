package com.ipb.platform.persistence;

import com.ipb.platform.persistence.entities.DatesNotWorkingEntity;
import com.ipb.platform.persistence.entities.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DatesNotWorkingRepository extends JpaRepository<DatesNotWorkingEntity, Long> {
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM dates_not_working d WHERE d.landmark.id = :landmarkId")
    void deleteByObjectId(@Param("landmarkId") Long landmarkId);
}
