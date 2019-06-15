package com.ipb.platform.persistance;

import com.ipb.platform.persistance.entities.CityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<CityEntity, Long> {

}
