package com.ipb.platform.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipb.platform.persistence.entities.CityEntity;

public interface CityRepository extends JpaRepository<CityEntity, Long> {

}
