package com.ipb.platform.services;

import java.util.List;

import com.ipb.platform.dto.responses.ImageResponseDTO;

public interface ImageService {
	
	List<ImageResponseDTO> findByObjectId(Long id);
	
	ImageResponseDTO findById(Long id);

	boolean deleteById(Long id);

	boolean deleteByObjectId(Long objectId);
}
