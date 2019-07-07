package com.ipb.platform.dto.responses;

import com.ipb.platform.persistence.entities.UserType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * An instance of this class is sent back to the user controller
 * after the user service is done manipulating the user request DTO
 * in some way.
 * 
 * @author dvt32
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
	
	private Long id;
	private String email;
	private String password;
	private String matchingPassword;
	private String firstName;
	private String lastName;
	private java.sql.Date birthday;
	private UserType type;

	private List<CategoryResponseDTO> categories;
	
}
