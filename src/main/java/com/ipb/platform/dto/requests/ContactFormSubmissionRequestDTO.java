package com.ipb.platform.dto.requests;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * An instance of this class is used 
 * when making HTTP requests related to users'
 * contact form submissions.
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
public class ContactFormSubmissionRequestDTO {
	
	private Long id;
	
	private String senderEmail;
	
	@NotBlank(message = "Form submission must not be null or blank!")
	@Column(length = 300)
	private String message;
	
	private String pathToAttachedFile;

}