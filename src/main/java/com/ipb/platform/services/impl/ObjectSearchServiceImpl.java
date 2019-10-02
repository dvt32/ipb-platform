package com.ipb.platform.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ipb.platform.persistence.ObjectRepository;
import com.ipb.platform.persistence.entities.ObjectEntity;
import com.ipb.platform.persistence.entities.ObjectType;
import com.ipb.platform.services.ObjectSearchService;

import lombok.AllArgsConstructor;

/**
 * This class is responsible for the business logic
 * when dealing with searching for objects in the system.
 * (searching by keyword, relevance etc.)
 * 
 * @author dvt32
 */
@Service
@AllArgsConstructor
public class ObjectSearchServiceImpl
	implements ObjectSearchService
{
	@Autowired
	private ObjectRepository objectRepository;
	
	/*
	 * ********************************
	 * Search by keyword functionality
	 * ********************************
	 */
	
	@Override
	public List<ObjectEntity> getObjectsContainingName(String name) {
		return objectRepository.findByNameContainingIgnoreCase(name);
	}
	
	@Override
	public List<ObjectEntity> getObjectsContainingDescription(String description) {
		return objectRepository.findByDescriptionContainingIgnoreCase(description);
	}

	@Override
	public List<ObjectEntity> getObjectsOfType(ObjectType type) {
		return objectRepository.findByType(type);
	}

	@Override
	public List<ObjectEntity> getObjectsContainingNameAndDescription(String name, String description) {
		return objectRepository.findByNameContainingIgnoreCaseAndDescriptionContainingIgnoreCase(name, description);
	}

	@Override
	public List<ObjectEntity> getObjectsContainingNameAndOfType(String name, ObjectType type) {
		return objectRepository.findByNameContainingIgnoreCaseAndType(name, type);
	}

	@Override
	public List<ObjectEntity> getObjectsContainingDescriptionAndOfType(String description, ObjectType type) {
		return objectRepository.findByDescriptionContainingIgnoreCaseAndType(description, type);
	}

	@Override
	public List<ObjectEntity> getObjectsContainingNameAndDescriptionAndOfType(
		String name, 
		String description, 
		ObjectType type) 
	{
		return objectRepository.findByNameContainingIgnoreCaseAndDescriptionContainingIgnoreCaseAndType(name, description, type);
	}
	
}