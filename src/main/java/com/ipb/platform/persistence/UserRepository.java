package com.ipb.platform.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipb.platform.persistence.entities.UserEntity;
import com.ipb.platform.persistence.entities.UserType;

/**
 * This interface allows CRUD operations via Spring Data JPA
 * on user entities. The methods defined below allow custom
 * data manipulation (the methods' implementation is provided by Spring Data JPA).
 * 
 * @author dvt32
 */
public interface UserRepository 
	extends JpaRepository<UserEntity, Long> 
{
	Optional<UserEntity> findByEmail(String email);
	Optional<UserEntity> findByType(UserType type);
	void deleteByEmail(String email);
}