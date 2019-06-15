package com.ipb.platform.mappers;

import org.springframework.stereotype.Service;

import com.ipb.platform.dto.requests.LandmarkRequestDTO;
import com.ipb.platform.dto.responses.LandmarkResponseDTO;
import com.ipb.platform.persistance.entities.LandmarkEntity;

public interface LandmarkMapper {
	
	LandmarkEntity toEntity(LandmarkRequestDTO landmark);
	
	LandmarkResponseDTO toDTO(LandmarkEntity landmarkEntity); 
}
