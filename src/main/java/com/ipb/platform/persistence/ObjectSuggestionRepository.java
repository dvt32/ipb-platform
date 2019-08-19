package com.ipb.platform.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipb.platform.persistence.entities.ObjectSuggestionEntity;

/**
 * This interface allows CRUD operations via Spring Data JPA
 * on object suggestion entities. The methods defined below allow custom
 * data manipulation (the methods' implementation is provided by Spring Data JPA).
 * 
 * @author dvt32
 */
public interface ObjectSuggestionRepository
	extends JpaRepository<ObjectSuggestionEntity, Long>
{

}
