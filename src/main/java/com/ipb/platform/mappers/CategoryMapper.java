package com.ipb.platform.mappers;

import com.ipb.platform.dto.requests.CategoryRequestDTO;
import com.ipb.platform.dto.responses.CategoryResponseDTO;
import com.ipb.platform.persistance.entities.CategoryEntity;

public interface CategoryMapper{
	
	CategoryEntity toEntity(CategoryRequestDTO city);
	
	CategoryResponseDTO toDTO(CategoryEntity cityEntity); 
}
