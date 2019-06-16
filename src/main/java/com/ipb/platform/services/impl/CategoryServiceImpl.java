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
		CategoryEntity entity = this.mapper.toEntity(category);
		
		// if parent id is empty category has not a parent
		if(category.getParentId() != null) {
			this.checkCategoryExist(
					category.getParentId(),
					"Tryed to add a category with non-existant parent category."
			);
		}

		CategoryEntity saveEntity = this.repository.save(entity);
		return saveEntity.getId();
	}

	@Override
	public CategoryResponseDTO update(Long categoryId, CategoryRequestDTO category) {
		CategoryEntity editCategoryEntity = this.checkCategoryExist(
				categoryId,
				"Tryed to edit non-existant category."
		);

		// if parent id is empty category has not a parent
		if(category.getParentId() != null) {
			// check the parent whether exists
			this.checkCategoryExist(category.getParentId(), "Tryed to set non-existant parent category.");
		}

		editCategoryEntity.setName(category.getName());
		editCategoryEntity.setParentId(category.getParentId());

		return this.mapper.toDTO(this.repository.save(editCategoryEntity));
	}

	@Override
	public List<CategoryResponseDTO> getAll() {
		return this.repository.findAll().stream()
				.map(entity -> this.mapper.toDTO(entity))
				.collect(Collectors.toList());
	}

	@Override
	public CategoryResponseDTO findById(Long id) {
		CategoryEntity category = this.checkCategoryExist(id, "Tryed to get non-existant category.");
		return this.mapper.toDTO(category);
	}

	@Override
	public boolean deleteById (Long categoryId) {
		this.checkCategoryExist(categoryId, "Tryed to delete non-existant category.");

		List<CategoryEntity> childrenCategories = this.repository.findAllByParentId(categoryId);
		if (childrenCategories != null && childrenCategories.size() > 0) {
			return false;
		}

		this.repository.deleteById(categoryId);
		return true;
	}


	@Override
	public List<CategoryResponseDTO> getChildrenByParentId (Long parentId) {

		this.checkCategoryExist(parentId, "Tryed to get childre from non-existant category.");

		return this.repository
				.findAllByParentId(parentId)
				.stream()
				.map(entity -> mapper.toDTO(entity))
				.collect(Collectors.toList());
	}

	/**
	 *
	 * @param categoryId - id of the category we want to check
	 * @param errorMessage if category not exist thow this message
	 * @return if category exist return category entity else throw IllegalArgumentException
	 */
	private CategoryEntity checkCategoryExist(Long categoryId, String errorMessage) {

		Optional<CategoryEntity> category = this.repository.findById(categoryId);

		if (!category.isPresent()) {
			throw new IllegalArgumentException(errorMessage);
		}

		return category.get();
	}
}
