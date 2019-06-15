package com.ipb.platform.persistance;

import com.ipb.platform.persistance.entities.ObjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ObjectRepository extends JpaRepository<ObjectEntity, Long> {

}
