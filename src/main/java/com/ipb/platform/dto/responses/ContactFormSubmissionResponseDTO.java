package com.ipb.platform.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * An instance of this class is sent back to the contact form submission controller
 * after the contact form submission service is done manipulating 
 * the contact form submission request DTO in some way.
 * 
 * @author dvt32
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactFormSubmissionResponseDTO {
	
	private Long id;
	private String senderEmail;
	private String message;
	private String pathToAttachedFile;
	
}