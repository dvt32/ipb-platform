package com.ipb.platform.persistence;

import com.ipb.platform.persistence.entities.ImageEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ImageRepository extends JpaRepository<ImageEntity, Long> {
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM images i WHERE i.object.id = :objectId")
    void deleteByObjectId(@Param("objectId") Long objectId);

    List<ImageEntity> findAllByObjectId(Long objectId);
}
