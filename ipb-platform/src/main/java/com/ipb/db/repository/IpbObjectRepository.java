package com.ipb.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipb.db.entity.IpbObject;

/**
 * Interface used for manipulating OBJECTS data in the database.
 * 
 * Placing an @Autowired annotation on a object of this type allows
 * us to create, read, update, delete and find data via save(), delete(),
 * findById() and other such methods.
 * 
 * The methods themselves are implemented by Spring Data JPA. 
 * The interface extends JpaRepository and specifies the repository's 
 * JPA entity class type and primary key type.
 * 
 * See Spring Data JPA docs for more information:
 *	- https://docs.spring.io/spring-data/jpa/docs/current/reference/html/
 */
public interface IpbObjectRepository extends JpaRepository<IpbObject, Long> {

}
