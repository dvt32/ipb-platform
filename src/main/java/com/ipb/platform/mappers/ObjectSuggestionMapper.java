package com.ipb.platform.mappers;

import com.ipb.platform.dto.requests.ObjectSuggestionRequestDTO;
import com.ipb.platform.dto.responses.ObjectSuggestionResponseDTO;
import com.ipb.platform.persistence.entities.ObjectSuggestionEntity;

/**
 * This interface specifies the method signatures for an object suggestion mapper implementation.
 * The purpose of the mapper implementation is to convert entity objects to DTOs (and vice-versa).
 * 
 * @author dvt32
 */
public interface ObjectSuggestionMapper {
	ObjectSuggestionEntity toEntity(ObjectSuggestionRequestDTO requestDTO);
	ObjectSuggestionResponseDTO toResponseDTO(ObjectSuggestionEntity entity);
	ObjectSuggestionRequestDTO toRequestDTO(ObjectSuggestionResponseDTO responseDTO);
}