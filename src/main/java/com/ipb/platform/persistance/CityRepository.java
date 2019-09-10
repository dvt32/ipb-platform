package com.ipb.platform.persistance;

import com.ipb.platform.persistance.entities.CityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CityRepository extends JpaRepository<CityEntity, Long> {
    List<CityEntity> findAllByName(String name);
}
