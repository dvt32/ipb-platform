package com.ipb.platform.persistence;

import com.ipb.platform.persistence.entities.RouteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RouteRepository extends JpaRepository<RouteEntity, Long> {
    List<RouteEntity> findAllByName(String name);

    List<RouteEntity> findAllByCreatorIdOrderByIdDesc(Long userId);
}
