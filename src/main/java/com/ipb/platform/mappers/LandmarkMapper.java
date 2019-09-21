package com.ipb.platform.mappers;

import com.ipb.platform.dto.requests.LandmarkRequestDTO;
import com.ipb.platform.dto.responses.LandmarkResponseDTO;
import com.ipb.platform.persistence.entities.LandmarkEntity;

public interface LandmarkMapper {
	
	LandmarkEntity toEntity(LandmarkRequestDTO landmark);
	
	LandmarkResponseDTO toDTO(LandmarkEntity landmarkEntity); 
}
