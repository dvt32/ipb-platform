package com.ipb.platform.mappers;

import com.ipb.platform.dto.requests.ImageRequestDTO;
import com.ipb.platform.dto.responses.ImageResponseDTO;
import com.ipb.platform.persistence.entities.ImageEntity;

public interface ImageMapping {
	
	ImageEntity toEntity(ImageRequestDTO imageDTO);
	
	ImageResponseDTO toDTO(ImageEntity imageEntity); 
}
