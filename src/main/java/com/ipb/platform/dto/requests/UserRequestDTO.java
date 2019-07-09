package com.ipb.platform.dto.requests;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.ipb.platform.persistence.entities.UserType;
import com.ipb.platform.validation.PasswordMatches;
import com.ipb.platform.validation.ValidEmail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * An instance of this class is used 
 * when making HTTP requests related to users 
 * (creating, retrieving, updating, deleting). 
 * 
 * The user controller takes this request object 
 * and sends it to the user service,
 * which will send a response object back
 * or it will inform the user whether the operation was successful or not.
 * 
 * Note: JPA annotations are added here, 
 * so that Spring can validate the request object 
 * before sending it to the user service.
 * 
 * @author dvt32
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@PasswordMatches
public class UserRequestDTO {
	
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
	private Long birthdayInMilliseconds;
	
	private UserType type;
	
}