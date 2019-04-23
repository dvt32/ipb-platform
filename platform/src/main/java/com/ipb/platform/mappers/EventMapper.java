package com.ipb.platform.mappers;

import com.ipb.platform.dto.requests.EventRequestDTO;
import com.ipb.platform.dto.responses.EventResponseDTO;
import com.ipb.platform.persistance.entities.EventEntity;

public interface EventMapper{
	
	EventEntity toEntity(EventRequestDTO eventDTO);
	
	EventResponseDTO toDTO(EventEntity eventEntity); 
}
