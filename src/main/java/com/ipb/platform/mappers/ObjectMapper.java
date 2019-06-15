package com.ipb.platform.mappers;

import com.ipb.platform.dto.requests.ObjectRequestDTO;
import com.ipb.platform.dto.responses.ObjectResponseDTO;
import com.ipb.platform.persistance.entities.ObjectEntity;
import com.ipb.platform.persistance.entities.ObjectType;

public interface ObjectMapper{
	
	ObjectEntity toEntity(ObjectRequestDTO objDTO, ObjectType type);
	
	ObjectResponseDTO toDTO(ObjectEntity objEntity, ObjectType type); 
}
