package com.ipb.platform.mappers.impl;

import org.springframework.stereotype.Service;

import com.ipb.platform.dto.requests.CategoryRequestDTO;
import com.ipb.platform.dto.responses.CategoryResponseDTO;
import com.ipb.platform.mappers.CategoryMapping;
import com.ipb.platform.persistance.entities.CategoryEntity;

import lombok.NoArgsConstructor;

@Service
@NoArgsConstructor
public class CategoryMapperImpl implements CategoryMapping {
	
	@Override
	public CategoryEntity toEntity(CategoryRequestDTO category) {
		
		CategoryEntity entity = new CategoryEntity(); 
		
		entity.setName(category.getName());
		entity.setParentId(category.getParentId());
		
		return entity;
	}

	@Override
	public CategoryResponseDTO toDTO(CategoryEntity category) {
		
		CategoryResponseDTO dto = new CategoryResponseDTO();
		
		dto.setId(category.getId());
		dto.setName(category.getName());
		
		dto.setParentId(category.getParentId());
		
		return dto;
	}
}
