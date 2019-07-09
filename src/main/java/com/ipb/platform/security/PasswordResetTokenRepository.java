package com.ipb.platform.security;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * A repository for CRUD operations with password reset tokens.
 * The implementation is provided by Spring Data JPA.
 * 
 * @author dvt32
 */
public interface PasswordResetTokenRepository 
	extends JpaRepository<PasswordResetToken, Long> 
{
	boolean existsByToken(String token);
	PasswordResetToken findByToken(String token);
}