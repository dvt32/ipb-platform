package com.ipb.platform.persistance;

import com.ipb.platform.persistance.entities.LandmarkEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LandmarkRepository extends JpaRepository<LandmarkEntity, Long> {

}
