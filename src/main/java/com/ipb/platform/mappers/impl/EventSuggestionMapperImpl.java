package com.ipb.platform.mappers.impl;

import org.springframework.stereotype.Service;

import com.ipb.platform.dto.requests.EventSuggestionRequestDTO;
import com.ipb.platform.dto.responses.EventSuggestionResponseDTO;
import com.ipb.platform.mappers.EventSuggestionMapper;
import com.ipb.platform.persistence.entities.EventSuggestionEntity;

import lombok.NoArgsConstructor;

/**
 * This class implements the event suggestion mapper interface. 
 * It is used to convert entity objects to DTOs (and vice-versa).
 * 
 * @author dvt32
 */
@Service
@NoArgsConstructor
public class EventSuggestionMapperImpl 
	implements EventSuggestionMapper
{
	
	@Override
	public EventSuggestionEntity toEntity(EventSuggestionRequestDTO requestDTO) {
		EventSuggestionEntity entity = new EventSuggestionEntity(); 
		
		entity.setId( requestDTO.getId() );
		entity.setSenderEmail( requestDTO.getSenderEmail() );
		entity.setName( requestDTO.getName() );
		entity.setDescription( requestDTO.getDescription() );
		entity.setCityId( requestDTO.getCityId() );
		entity.setStartDate( requestDTO.getStartDate() );
		entity.setEndDate( requestDTO.getEndDate() );
		entity.setLatitude( requestDTO.getLatitude() );
		entity.setLongitude( requestDTO.getLongitude() );
		
		return entity;
	}

	@Override
	public EventSuggestionResponseDTO toResponseDTO(EventSuggestionEntity entity) {
		EventSuggestionResponseDTO responseDTO = new EventSuggestionResponseDTO();
		
		responseDTO.setId( entity.getId() );
		responseDTO.setSenderEmail( entity.getSenderEmail() );
		responseDTO.setName( entity.getName() );
		responseDTO.setDescription( entity.getDescription() );
		responseDTO.setCityId( entity.getCityId() );
		responseDTO.setStartDate( entity.getStartDate() );
		responseDTO.setEndDate( entity.getEndDate() );
		responseDTO.setLatitude( entity.getLatitude() );
		responseDTO.setLongitude( entity.getLongitude() );
		
		return responseDTO;
	}

	@Override
	public EventSuggestionRequestDTO toRequestDTO(EventSuggestionResponseDTO responseDTO) {
		EventSuggestionRequestDTO requestDTO = new EventSuggestionRequestDTO();
		
		requestDTO.setId( responseDTO.getId() );
		requestDTO.setSenderEmail( responseDTO.getSenderEmail() );
		requestDTO.setName( responseDTO.getName() );
		requestDTO.setDescription( responseDTO.getDescription() );
		requestDTO.setCityId( responseDTO.getCityId() );
		requestDTO.setStartDate( responseDTO.getStartDate() );
		requestDTO.setEndDate( responseDTO.getEndDate() );
		requestDTO.setLatitude( responseDTO.getLatitude() );
		requestDTO.setLongitude( responseDTO.getLongitude() );
		
		return requestDTO;
	}
	
}