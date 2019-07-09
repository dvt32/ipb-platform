package com.ipb.platform.security;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import com.ipb.platform.persistence.entities.UserEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This class represents a password reset token in the system. 
 * The token is a random UUID (Universally Unique Identifier) string 
 * that is unique for all practical purposes.
 * 
 * The token is generated and appended to a reset password link, which
 * opens up a page for resetting a user's password (provided that the token is valid).
 * 
 * @author dvt32
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
public class PasswordResetToken {

	@Id
	@GeneratedValue
	private Long id;
	
	private String token;
	
	@OneToOne
	UserEntity user;
	
}