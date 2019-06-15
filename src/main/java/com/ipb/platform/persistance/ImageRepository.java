package com.ipb.platform.persistance;

import com.ipb.platform.persistance.entities.ImageEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<ImageEntity, Long> {

}
