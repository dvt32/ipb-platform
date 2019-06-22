package com.ipb.platform.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipb.platform.persistence.entities.EventEntity;

public interface EventRepository extends JpaRepository<EventEntity, Long> {

}
