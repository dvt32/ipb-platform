package com.ipb.platform.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ipb.platform.dto.requests.ObjectRequestDTO;
import com.ipb.platform.dto.responses.ObjectResponseDTO;
import com.ipb.platform.mappers.ObjectMapper;
import com.ipb.platform.persistence.CategoryRepository;
import com.ipb.platform.persistence.ObjectRepository;
import com.ipb.platform.persistence.entities.CategoryEntity;
import com.ipb.platform.persistence.entities.ObjectEntity;
import com.ipb.platform.persistence.entities.ObjectType;
import com.ipb.platform.services.ImageService;
import com.ipb.platform.services.ObjectService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ObjectServiceImpl implements ObjectService {

	private ObjectRepository objectRepository;
	private CategoryRepository categoryRepository;
	
	private ObjectMapper objectMapper;
	private ImageService imageService;
	
	@Override
	public Long save(ObjectRequestDTO obj) {
		ObjectEntity entity = this.objectMapper
				.toEntity(obj, ObjectType.valueOf(obj.getType())); 
		
		if (obj.getCategoriesID() != null) {
			// check whether all categories exists.
			List<CategoryEntity> categoriesEntities = obj.getCategoriesID().stream().map((catID) -> {
				Optional<CategoryEntity> category = this.categoryRepository.findById(catID);
				
				// check category exists.
				if (!category.isPresent()) {
					throw new IllegalArgumentException("Tryed to add a object for non-existant category.");
				}
				
				CategoryEntity catEntity = category.get();
				
				// update category objects
				catEntity.getObjects().add(entity);
				catEntity.setObjects(catEntity.getObjects());
				
				// save categories changes
				//this.categoryRepository.save(catEntity);
				
				return catEntity;
			})
			.collect(Collectors.toList());
			System.out.println("[[ categoriesEntities ]]" + categoriesEntities);
			entity.setCategories(categoriesEntities);
		}
		
		ObjectEntity objectEntity = this.objectRepository.save(entity);
		
		// save all images
		this.imageService.save(obj.getImages(), objectEntity);
			
		return objectEntity.getId();
	}

	@Override
	public List<ObjectResponseDTO> getAll() {
		return this.objectRepository.findAll().stream()
				.map(entity -> objectMapper.toDTO(entity, entity.getType()))
				.collect(Collectors.toList());
	}

	@Override
	public ObjectResponseDTO findById(Long id) {

		Optional<ObjectEntity> objEntity = this.objectRepository.findById(id);

		// check category exists.
		if (!objEntity.isPresent()) {
			throw new IllegalArgumentException("Tryed to get non-existant object.");
		}

		return this.objectMapper.toDTO(objEntity.get(), null);
	}

}
