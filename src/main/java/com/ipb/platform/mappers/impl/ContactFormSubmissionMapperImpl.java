package com.ipb.platform.mappers.impl;

import org.springframework.stereotype.Service;

import com.ipb.platform.dto.requests.ContactFormSubmissionRequestDTO;
import com.ipb.platform.dto.responses.ContactFormSubmissionResponseDTO;
import com.ipb.platform.mappers.ContactFormSubmissionMapper;
import com.ipb.platform.persistence.entities.ContactFormSubmissionEntity;

import lombok.NoArgsConstructor;

/**
 * This class implements the contact form submission mapper interface. 
 * It is used to convert entity objects to DTOs (and vice-versa).
 * 
 * @author dvt32
 */
@Service
@NoArgsConstructor
public class ContactFormSubmissionMapperImpl 
	implements ContactFormSubmissionMapper
{
	
	@Override
	public ContactFormSubmissionEntity toEntity(ContactFormSubmissionRequestDTO requestDTO) {
		ContactFormSubmissionEntity entity = new ContactFormSubmissionEntity(); 
		
		entity.setId( requestDTO.getId() );
		entity.setSenderEmail( requestDTO.getSenderEmail() );
		entity.setMessage( requestDTO.getMessage() );
		entity.setPathToAttachedFile( requestDTO.getPathToAttachedFile() );
		
		return entity;
	}

	@Override
	public ContactFormSubmissionResponseDTO toResponseDTO(ContactFormSubmissionEntity entity) {
		ContactFormSubmissionResponseDTO responseDTO = new ContactFormSubmissionResponseDTO();
		
		responseDTO.setId( entity.getId() );
		responseDTO.setSenderEmail( entity.getSenderEmail() );
		responseDTO.setMessage( entity.getMessage() );
		responseDTO.setPathToAttachedFile( entity.getPathToAttachedFile() );
		
		return responseDTO;
	}

	@Override
	public ContactFormSubmissionRequestDTO toRequestDTO(ContactFormSubmissionResponseDTO responseDTO) {
		ContactFormSubmissionRequestDTO requestDTO = new ContactFormSubmissionRequestDTO();
		
		requestDTO.setId( responseDTO.getId() );
		requestDTO.setSenderEmail( responseDTO.getSenderEmail() );
		requestDTO.setMessage( responseDTO.getMessage() );
		requestDTO.setPathToAttachedFile( responseDTO.getPathToAttachedFile() );
		
		return requestDTO;
	}

}
