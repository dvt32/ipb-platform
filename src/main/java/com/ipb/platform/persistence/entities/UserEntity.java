package com.ipb.platform.persistence.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.ipb.platform.validation.PasswordMatches;
import com.ipb.platform.validation.ValidEmail;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This class represents a "user" entity in the database.
 * Each user has a type, which can either be "USER" or "ADMIN".
 * 
 * @author dvt32
 */
@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name="Users")
@PasswordMatches
public class UserEntity {
	
	@Id
	@GeneratedValue
	private Long id;
	
	@NotBlank(message = "User e-mail must not be null or blank!")
	@ValidEmail
	@Column(length = 100)
	@Size(max = 100)
	private String email;
	
	@NotBlank(message = "User password must not be null or blank!")
	@Column(length = 100)
	@Size(min = 6, message = "User password must be at least 6 characters!")
	private String password;
	private String matchingPassword;
	
	@NotBlank(message = "First name must not be null or blank!")
	@Column(length = 255)
	@Size(max = 255)
	private String firstName;
	
	@NotBlank(message = "Last name must not be null or blank!")
	@Column(length = 255)
	@Size(max = 255)
	private String lastName;
	
	@NotNull(message = "Birth date must not be be null!")
    private java.sql.Date birthday;
	
	@Enumerated(EnumType.STRING)
	private UserType type;
	
}