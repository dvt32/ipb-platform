package com.ipb.platform.mappers;

import com.ipb.platform.dto.requests.EventSuggestionRequestDTO;
import com.ipb.platform.dto.responses.EventSuggestionResponseDTO;
import com.ipb.platform.persistence.entities.EventSuggestionEntity;

/**
 * This interface specifies the method signatures for an event suggestion mapper implementation.
 * The purpose of the mapper implementation is to convert entity objects to DTOs (and vice-versa).
 * 
 * @author dvt32
 */
public interface EventSuggestionMapper {
	EventSuggestionEntity toEntity(EventSuggestionRequestDTO requestDTO);
	EventSuggestionResponseDTO toResponseDTO(EventSuggestionEntity entity);
	EventSuggestionRequestDTO toRequestDTO(EventSuggestionResponseDTO responseDTO);
}