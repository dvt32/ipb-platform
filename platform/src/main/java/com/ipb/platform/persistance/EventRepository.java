package com.ipb.platform.persistance;

import com.ipb.platform.persistance.entities.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<EventEntity, Long> {

}
