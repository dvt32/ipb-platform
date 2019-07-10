package com.ipb.platform.services;

import java.util.List;

import com.ipb.platform.dto.requests.ImageRequestDTO;
import com.ipb.platform.dto.responses.ImageResponseDTO;
import com.ipb.platform.persistence.entities.ObjectEntity;

public interface ImageService {
	
	Long save(ImageRequestDTO image);
	
	boolean save(List<ImageRequestDTO> images, ObjectEntity objectEntity);
	
	List<ImageResponseDTO> findByObjectId(Long id);
	
	ImageResponseDTO findById(Long id);
}
