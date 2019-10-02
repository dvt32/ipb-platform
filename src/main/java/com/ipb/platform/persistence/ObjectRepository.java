package com.ipb.platform.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipb.platform.persistence.entities.ObjectEntity;
import com.ipb.platform.persistence.entities.ObjectType;

/**
 * This interface allows CRUD operations via Spring Data JPA
 * on object entities. The methods defined below allow custom
 * data manipulation (the methods' implementation is provided by Spring Data JPA).
 * 
 * @author dvt32
 */
public interface ObjectRepository 
	extends JpaRepository<ObjectEntity, Long> 
{
	List<ObjectEntity> findByNameContainingIgnoreCase(String name);
	List<ObjectEntity> findByDescriptionContainingIgnoreCase(String description);
	List<ObjectEntity> findByType(ObjectType type);
	List<ObjectEntity> findByNameContainingIgnoreCaseAndDescriptionContainingIgnoreCase(String name, String description);
	List<ObjectEntity> findByNameContainingIgnoreCaseAndType(String name, ObjectType type);
	List<ObjectEntity> findByDescriptionContainingIgnoreCaseAndType(String description, ObjectType type);
	List<ObjectEntity> findByNameContainingIgnoreCaseAndDescriptionContainingIgnoreCaseAndType(String name, String description, ObjectType type);
}