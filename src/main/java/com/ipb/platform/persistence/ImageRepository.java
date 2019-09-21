package com.ipb.platform.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipb.platform.persistence.entities.ImageEntity;

public interface ImageRepository extends JpaRepository<ImageEntity, Long> {

}
