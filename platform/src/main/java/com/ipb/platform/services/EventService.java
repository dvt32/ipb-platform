package com.ipb.platform.services;

import java.util.List;

import com.ipb.platform.dto.requests.EventRequestDTO;
import com.ipb.platform.dto.responses.EventResponseDTO;

public interface EventService {
	
	boolean save(EventRequestDTO event);
	
	List<EventResponseDTO> getAll();
	
	EventResponseDTO findById(Long id);
}
