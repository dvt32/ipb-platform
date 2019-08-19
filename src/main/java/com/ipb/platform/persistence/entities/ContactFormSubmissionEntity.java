package com.ipb.platform.persistence.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This class represents a "contact form submission" entity in the database.

 * @author dvt32
 */
@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name="Contact_Form_Submissions")
public class ContactFormSubmissionEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "contact_form_submission_sequence")
	private Long id;
	
	@NotBlank(message = "Sender e-mail must not be null or blank!")
	private String senderEmail;
	
	@NotBlank(message = "Form submission must not be null or blank!")
	@Column(length = 300)
	private String message;
	
	private String pathToAttachedFile;
	
}