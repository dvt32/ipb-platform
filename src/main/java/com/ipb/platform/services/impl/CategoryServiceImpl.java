package com.ipb.platform.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ipb.platform.dto.requests.CategoryRequestDTO;
import com.ipb.platform.dto.responses.CategoryResponseDTO;
import com.ipb.platform.mappers.CategoryMapper;
import com.ipb.platform.mappers.ObjectMapper;
import com.ipb.platform.persistance.CategoryRepository;
import com.ipb.platform.persistance.ObjectRepository;
import com.ipb.platform.persistance.entities.CategoryEntity;
import com.ipb.platform.persistance.entities.CityEntity;
import com.ipb.platform.persistance.entities.ImageEntity;
import com.ipb.platform.persistance.entities.ObjectEntity;
import com.ipb.platform.services.CategoryService;
import com.ipb.platform.services.ImageService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService{
	
	private CategoryRepository repository;
	
	private CategoryMapper mapper;
	
	@Override
	public Long save(CategoryRequestDTO category) {
		CategoryEntity entiity = this.mapper.toEntity(category);
		
		// if parent id is empty category has not a parent
		System.out.println(category);
		if(category.getParentId() != null) {
			Optional<CategoryEntity> parentCategory = this.repository.findById(category.getParentId());
			
			if (!parentCategory.isPresent()) {
				throw new IllegalArgumentException("Tryed to add a category with non-existant parent category.");
			}
		}

		CategoryEntity saveEntiity = this.repository.save(entiity);
		return saveEntiity.getId();
	}

	@Override
	public List<CategoryResponseDTO> getAll() {
		return this.repository.findAll().stream()
				.map(entity -> this.mapper.toDTO(entity))
				.collect(Collectors.toList());
	}

	@Override
	public CategoryResponseDTO findById(Long id) {
		return this.mapper.toDTO(this.repository.findById(id).get());
	}

}
