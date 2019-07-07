package com.ipb.platform.mappers;

import com.ipb.platform.dto.requests.CategoryRequestDTO;
import com.ipb.platform.dto.responses.CategoryResponseDTO;
import com.ipb.platform.persistence.entities.CategoryEntity;

public interface CategoryMapping {
	
	CategoryEntity toEntity(CategoryRequestDTO city);
	
	CategoryResponseDTO toDTO(CategoryEntity cityEntity); 
}
