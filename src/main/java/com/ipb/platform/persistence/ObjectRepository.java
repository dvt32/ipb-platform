package com.ipb.platform.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipb.platform.persistence.entities.ObjectEntity;

public interface ObjectRepository extends JpaRepository<ObjectEntity, Long> {

}
