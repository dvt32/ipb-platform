package com.ipb.platform.mappers;

import com.ipb.platform.dto.requests.ContactFormSubmissionRequestDTO;
import com.ipb.platform.dto.responses.ContactFormSubmissionResponseDTO;
import com.ipb.platform.persistence.entities.ContactFormSubmissionEntity;

/**
 * This interface specifies the method signatures for a contact form submission mapper implementation.
 * The purpose of the mapper implementation is to convert entity objects to DTOs (and vice-versa).
 * 
 * @author dvt32
 */
public interface ContactFormSubmissionMapper {
	ContactFormSubmissionEntity toEntity(ContactFormSubmissionRequestDTO requestDTO);
	ContactFormSubmissionResponseDTO toResponseDTO(ContactFormSubmissionEntity entity);
	ContactFormSubmissionRequestDTO toRequestDTO(ContactFormSubmissionResponseDTO responseDTO);
}