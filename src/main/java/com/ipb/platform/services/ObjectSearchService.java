package com.ipb.platform.services;

import java.util.List;

import com.ipb.platform.persistence.entities.ObjectEntity;
import com.ipb.platform.persistence.entities.ObjectType;

/**
 * This interface specifies the list of operations 
 * allowed when searching for objects in the database.
 * 
 * @author dvt32
 */
public interface ObjectSearchService {
	List<ObjectEntity> getObjectsContainingName(String name);
	
	List<ObjectEntity> getObjectsContainingDescription(String description);
	
	List<ObjectEntity> getObjectsOfType(ObjectType type);
	
	List<ObjectEntity> getObjectsContainingNameAndDescription(String name, String description);
	
	List<ObjectEntity> getObjectsContainingNameAndOfType(String name, ObjectType type);
	
	List<ObjectEntity> getObjectsContainingDescriptionAndOfType(String description, ObjectType type);
	
	List<ObjectEntity> getObjectsContainingNameAndDescriptionAndOfType(String name, String description, ObjectType type);
}