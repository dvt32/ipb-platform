package com.ipb.platform.persistence;

import com.ipb.platform.persistence.entities.CityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CityRepository extends JpaRepository<CityEntity, Long> {
    List<CityEntity> findAllByName(String name);
}
