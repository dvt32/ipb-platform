package com.ipb.platform.services;

import java.util.List;

import com.ipb.platform.dto.requests.CategoryRequestDTO;
import com.ipb.platform.dto.responses.CategoryResponseDTO;
import com.ipb.platform.persistance.entities.ObjectEntity;

public interface CategoryService {
	
	Long save(CategoryRequestDTO category);

	/**
	 *
	 * @param categoryId contain category id we want to edit
	 * @param category contain new data
	 * @return update category
	 */
	CategoryResponseDTO update(Long categoryId, CategoryRequestDTO category);

	List<CategoryResponseDTO> getAll();
	
	CategoryResponseDTO findById(Long id);

	boolean deleteById(Long categoryId);

	List<CategoryResponseDTO> getChildrenByParentId (Long parentId);

}
