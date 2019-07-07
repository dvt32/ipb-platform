package com.ipb.platform.mappers;

import com.ipb.platform.dto.requests.RouteRequestDTO;
import com.ipb.platform.dto.responses.RouteResponseDTO;
import com.ipb.platform.persistence.entities.RouteEntity;

public interface RouteMapping {
	
	RouteEntity toEntity(RouteRequestDTO imageDTO);
	
	RouteResponseDTO toDTO(RouteEntity routeEntity);
}
