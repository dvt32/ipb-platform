package com.ipb.platform.dto.responses;

import com.ipb.platform.persistence.entities.ObjectType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * An instance of this class is sent back to the contact form submission controller
 * after the contact form submission service is done manipulating 
 * the object suggestion request DTO in some way.
 * 
 * @author dvt32
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ObjectSuggestionResponseDTO {
	
	private Long id;
	private String senderEmail;
	private String name;
	private String description;
	private double latitude;
	private double longitude;
	private ObjectType type;
	
}