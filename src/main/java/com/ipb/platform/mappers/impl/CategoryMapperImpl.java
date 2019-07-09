package com.ipb.platform.mappers.impl;

import org.springframework.stereotype.Service;

import com.ipb.platform.dto.requests.CategoryRequestDTO;
import com.ipb.platform.dto.responses.CategoryResponseDTO;
import com.ipb.platform.mappers.CategoryMapper;
import com.ipb.platform.persistence.entities.CategoryEntity;
import com.ipb.platform.persistence.entities.ObjectEntity;
import com.ipb.platform.persistence.entities.ObjectType;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Service
@NoArgsConstructor
public class CategoryMapperImpl implements CategoryMapper{
	
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
