package com.ipb.platform.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.ipb.platform.exceptions.IllegalRequestArgumentException;
import org.springframework.stereotype.Service;

import com.ipb.platform.dto.requests.CategoryRequestDTO;
import com.ipb.platform.dto.responses.CategoryResponseDTO;
import com.ipb.platform.mappers.CategoryMapping;
import com.ipb.platform.persistance.CategoryRepository;
import com.ipb.platform.persistance.entities.CategoryEntity;
import com.ipb.platform.services.CategoryService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService{
	
	private CategoryRepository repository;
	
	private CategoryMapping mapper;
	
	@Override
	public Long save(CategoryRequestDTO category) {
		CategoryEntity entity = this.mapper.toEntity(category);
		
		if(category.getParentId() != null) {
			this.getCategoryEntityOrThrow(
					category.getParentId(),
					"Tryed to add a category with non-existant parent category."
			);
		}

		CategoryEntity saveEntity = this.repository.save(entity);
		return saveEntity.getId();
	}

	@Override
	public CategoryResponseDTO update(Long categoryId, CategoryRequestDTO category) {
		CategoryEntity editCategoryEntity = this.getCategoryEntityOrThrow(
				categoryId,
				"Tryed to edit non-existant category."
		);

		// if parent id is empty category has not a parent
		System.out.println(category);
		if(category.getParentId() != null) {
			this.getCategoryEntityOrThrow(category.getParentId(), "Tryed to set non-existant parent category.");
			Optional<CategoryEntity> parentCategory = this.repository.findById(category.getParentId());

			if (!parentCategory.isPresent()) {
				throw new IllegalArgumentException("Tryed to add a category with non-existant parent category.");
			}
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
		CategoryEntity category = this.getCategoryEntityOrThrow(id, "Tryed to get non-existant category.");
		return this.mapper.toDTO(category);
	}

	@Override
	public CategoryEntity findCategoryEntityById(Long id) {
		return this.getCategoryEntityOrThrow(id, "Tryed to get non-existant category.");
	}

	@Override
	public boolean deleteById (Long categoryId) {
		this.getCategoryEntityOrThrow(categoryId, "Tryed to delete non-existant category.");

		List<CategoryEntity> childrenCategories = this.repository.findAllByParentId(categoryId);
		if (childrenCategories != null && childrenCategories.size() > 0) {
			return false;
		}

		this.repository.deleteById(categoryId);
		return true;
	}


	@Override
	public List<CategoryResponseDTO> getChildrenByParentId (Long parentId) {

		this.getCategoryEntityOrThrow(parentId, "Tryed to get childre from non-existant category.");

		return this.repository
				.findAllByParentId(parentId)
				.stream()
				.map(entity -> mapper.toDTO(entity))
				.collect(Collectors.toList());
	}

	private CategoryEntity getCategoryEntityOrThrow(Long categoryId, String errorMessage) {

		Optional<CategoryEntity> category = this.repository.findById(categoryId);

		if (!category.isPresent()) {
			throw new IllegalRequestArgumentException(errorMessage);
		}

		return category.get();
	}
}
