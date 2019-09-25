package com.ipb.platform.security;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.ipb.platform.persistence.entities.UserEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * This class represents a password reset token in the system. 
 * The token is a random UUID (Universally Unique Identifier) string 
 * that is unique for all practical purposes.
 * 
 * The token is generated and appended to a reset password link, which
 * opens up a page for resetting a user's password (provided that the token is valid).
 * 
 * The Hibernate-specific annotation @OnDelete is used
 * so that when the token's associated user has been deleted,
 * the token is automatically deleted as well.
 * 
 * @author dvt32
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class PasswordResetToken {

	@Id
	@GeneratedValue
	private Long id;
	
	private String token;
	
	@OneToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
	private UserEntity user;
	
}