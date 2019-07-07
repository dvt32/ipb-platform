package com.ipb.platform.mappers;

import com.ipb.platform.dto.requests.ObjectRequestDTO;
import com.ipb.platform.dto.responses.ObjectResponseDTO;
import com.ipb.platform.persistence.entities.ObjectEntity;

public interface IPBObjectMapping {
	
	ObjectEntity toEntity(ObjectRequestDTO objDTO);
	
	ObjectResponseDTO toDTO(ObjectEntity objEntity);
}
