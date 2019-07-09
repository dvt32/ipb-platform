package com.ipb.platform.mappers;

import com.ipb.platform.dto.requests.CityRequestDTO;
import com.ipb.platform.dto.responses.CityResponseDTO;
import com.ipb.platform.persistence.entities.CityEntity;

public interface CityMapper{
	
	CityEntity toEntity(CityRequestDTO city);
	
	CityResponseDTO toDTO(CityEntity cityEntity); 
}
