package com.ipb.platform.mappers.impl;

import org.springframework.stereotype.Service;

import com.ipb.platform.dto.requests.ObjectSuggestionRequestDTO;
import com.ipb.platform.dto.responses.ObjectSuggestionResponseDTO;
import com.ipb.platform.mappers.ObjectSuggestionMapper;
import com.ipb.platform.persistence.entities.ObjectSuggestionEntity;

import lombok.NoArgsConstructor;

/**
 * This class implements the object suggestion mapper interface. 
 * It is used to convert entity objects to DTOs (and vice-versa).
 * 
 * @author dvt32
 */
@Service
@NoArgsConstructor
public class ObjectSuggestionMapperImpl 
	implements ObjectSuggestionMapper
{
	
	@Override
	public ObjectSuggestionEntity toEntity(ObjectSuggestionRequestDTO requestDTO) {
		ObjectSuggestionEntity entity = new ObjectSuggestionEntity(); 
		
		entity.setId( requestDTO.getId() );
		entity.setSenderEmail( requestDTO.getSenderEmail() );
		entity.setName( requestDTO.getName() );
		entity.setDescription( requestDTO.getDescription() );
		entity.setLatitude( requestDTO.getLatitude() );
		entity.setLongitude( requestDTO.getLongitude() );
		entity.setType( requestDTO.getType() );
		
		return entity;
	}

	@Override
	public ObjectSuggestionResponseDTO toResponseDTO(ObjectSuggestionEntity entity) {
		ObjectSuggestionResponseDTO responseDTO = new ObjectSuggestionResponseDTO();
		
		responseDTO.setId( entity.getId() );
		responseDTO.setSenderEmail( entity.getSenderEmail() );
		responseDTO.setName( entity.getName() );
		responseDTO.setDescription( entity.getDescription() );
		responseDTO.setLatitude( entity.getLatitude() );
		responseDTO.setLongitude( entity.getLongitude() );
		responseDTO.setType( entity.getType() );
		
		return responseDTO;
	}

	@Override
	public ObjectSuggestionRequestDTO toRequestDTO(ObjectSuggestionResponseDTO responseDTO) {
		ObjectSuggestionRequestDTO requestDTO = new ObjectSuggestionRequestDTO();
		
		requestDTO.setId( responseDTO.getId() );
		requestDTO.setSenderEmail( responseDTO.getSenderEmail() );
		requestDTO.setName( responseDTO.getName() );
		requestDTO.setDescription( responseDTO.getDescription() );
		requestDTO.setLatitude( responseDTO.getLatitude() );
		requestDTO.setLongitude( responseDTO.getLongitude() );
		requestDTO.setType( responseDTO.getType() );
		
		return requestDTO;
	}

}
