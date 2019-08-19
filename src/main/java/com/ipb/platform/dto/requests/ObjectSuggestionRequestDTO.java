package com.ipb.platform.dto.requests;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;

import com.ipb.platform.persistence.entities.ObjectType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * An instance of this class is used 
 * when making HTTP requests related to users'
 * object suggestions (made via the contact form).
 * 
 * Note: JPA annotations are added here, 
 * so that Spring can validate the request object 
 * before sending it to the contact form submission service.
 * 
 * @author dvt32
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ObjectSuggestionRequestDTO {
	
	private Long id;
	
	private String senderEmail;
	
	@NotBlank(message = "Suggested object's name must not be null or blank!")
	private String name;
	
	@NotBlank(message = "Suggested object's description must not be null or blank!")
	private String description;
	
	private double latitude;
	
	private double longitude;
	
	@Enumerated(EnumType.STRING)
	private ObjectType type;

}