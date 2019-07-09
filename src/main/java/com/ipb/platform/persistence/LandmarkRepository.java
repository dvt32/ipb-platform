package com.ipb.platform.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipb.platform.persistence.entities.LandmarkEntity;

public interface LandmarkRepository extends JpaRepository<LandmarkEntity, Long> {

}
