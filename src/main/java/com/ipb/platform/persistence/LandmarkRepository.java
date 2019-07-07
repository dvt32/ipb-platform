package com.ipb.platform.persistence;

import com.ipb.platform.persistence.entities.LandmarkEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LandmarkRepository extends JpaRepository<LandmarkEntity, Long> {
    List<LandmarkEntity> findAllByName(String name);
}
