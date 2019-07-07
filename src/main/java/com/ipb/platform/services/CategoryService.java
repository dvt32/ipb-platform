package com.ipb.platform.services;

import java.util.List;

import com.ipb.platform.dto.requests.CategoryRequestDTO;
import com.ipb.platform.dto.responses.CategoryResponseDTO;
import com.ipb.platform.persistence.entities.CategoryEntity;
import com.ipb.platform.persistence.entities.ObjectEntity;

/**
 *  Description of all possible operations that can be performed on a given table of categories
 */
public interface CategoryService {
	/**
	 *
	 * @param category contains all the data for the new category
	 * @return category id which is generated automatically
	 */
	Long save(CategoryRequestDTO category);

	/**
	 *
	 * @param categoryId contain category id we want to edit
	 * @param category contain new category data
	 * @return update category
	 */
	CategoryResponseDTO update(Long categoryId, CategoryRequestDTO category);

	/**
	 *
	 * @return List<CategoryResponseDTO> which contains all categories information
	 */
	List<CategoryResponseDTO> getAll();

	/**
	 *
	 * @param id category id to which we want to receive the content
	 * @return search category information
	 */
	CategoryResponseDTO findById(Long id);

	/**
	 *
	 * @param id category id to which we want to receive the content
	 * @return search category entity data
	 */
	CategoryEntity findCategoryEntityById(Long id);

	/**
	 *
	 * @param categoryId category id to which we want to delete the content
	 * @return true if category deleted successfully else return false
	 */
	boolean deleteById(Long categoryId);

	/**
	 *
	 * @param parentId to which we want to get all children categories
	 * @return list of children categories data
	 */
	List<CategoryResponseDTO> getChildrenByParentId (Long parentId);



}
