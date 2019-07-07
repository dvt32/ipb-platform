package com.ipb.platform.persistence;

import com.ipb.platform.persistence.entities.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<EventEntity, Long> {
    List<EventEntity> findAllByName(String name);
}
