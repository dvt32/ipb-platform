package com.ipb.platform.persistance;

import com.ipb.platform.persistance.entities.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<EventEntity, Long> {
    List<EventEntity> findAllByName(String name);
}
