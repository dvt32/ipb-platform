package com.ipb.platform.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ipb.platform.persistence.entities.ObjectEntity;
import com.ipb.platform.persistence.entities.ObjectType;
import com.ipb.platform.services.ObjectSearchService;

import lombok.AllArgsConstructor;

/**
 * This controller allows search operations 
 * for objects in the database via HTTP requests.
 * 
 * @author dvt32
 */
@AllArgsConstructor
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/search")
public class ObjectSearchController {
	
	@Autowired
	private ObjectSearchService objectSearchService;
	
	/**
	 * The method retrieves objects from the database, which contain a certain keyword
	 * in their name and/or in their description (case insensitive).
	 * 
	 * Additionally, the user can limit the search by passing in the object's type.
	 * 
	 * @param name The passed name keyword
	 * @param description The passed description keyword
	 * @param type The object's type
	 * @return a list of retrieved objects, which match the criteria, or a ResponseEntity with an error message (if no arguments passed)
	 */
	@GetMapping
	public ResponseEntity<?> getObjectsMatching(
		@RequestParam(required=false) String name,
		@RequestParam(required=false) String description,
		@RequestParam(required=false) ObjectType type) 
	{
		boolean namePassed = (name != null);
		boolean descPassed = (description != null);
		boolean typePassed = (type != null);
		
		// if no args passed
		if (!namePassed && !descPassed && !typePassed) {
			return new ResponseEntity<>("No name, description or type specified!", HttpStatus.BAD_REQUEST);
		}
		// if only 1 arg passed
		else if (namePassed && !descPassed && !typePassed) {
			List<ObjectEntity> objectsContainingName = objectSearchService.getObjectsContainingName(name);
			return new ResponseEntity<>(objectsContainingName, HttpStatus.OK);
		}
		else if (!namePassed && descPassed && !typePassed) {
			List<ObjectEntity> objectsContainingDescription = objectSearchService.getObjectsContainingDescription(description);
			return new ResponseEntity<>(objectsContainingDescription, HttpStatus.OK);
		}
		else if (!namePassed && !descPassed && typePassed) {
			List<ObjectEntity> objectsOfType = objectSearchService.getObjectsOfType(type);
			return new ResponseEntity<>(objectsOfType, HttpStatus.OK);
		}
		// if 2 args passed
		else if (namePassed && descPassed && !typePassed) {
			List<ObjectEntity> objectsContainingNameAndDescription = objectSearchService.getObjectsContainingNameAndDescription(name, description);
			return new ResponseEntity<>(objectsContainingNameAndDescription, HttpStatus.OK);
		}
		else if (namePassed && !descPassed && typePassed) {
			List<ObjectEntity> objectsContainingNameAndOfType = objectSearchService.getObjectsContainingNameAndOfType(name, type);
			return new ResponseEntity<>(objectsContainingNameAndOfType, HttpStatus.OK);
		}
		else if (!namePassed && descPassed && typePassed) {
			List<ObjectEntity> objectsContainingDescriptionAndOfType = objectSearchService.getObjectsContainingDescriptionAndOfType(description, type);
			return new ResponseEntity<>(objectsContainingDescriptionAndOfType, HttpStatus.OK);
		}
		// if all 3 args passed
		else {
			List<ObjectEntity> objectsContainingNameAndDescriptionAndOfType = objectSearchService.getObjectsContainingNameAndDescriptionAndOfType(name, description, type);
			return new ResponseEntity<>(objectsContainingNameAndDescriptionAndOfType, HttpStatus.OK);
		}
	}

}