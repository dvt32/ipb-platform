package com.ipb.platform.services;

import java.util.List;

import com.ipb.platform.dto.requests.CategoryRequestDTO;
import com.ipb.platform.dto.responses.CategoryResponseDTO;

public interface CategoryService {
	
	Long save(CategoryRequestDTO category);
	
	List<CategoryResponseDTO> getAll();
	
	CategoryResponseDTO findById(Long id);
}
