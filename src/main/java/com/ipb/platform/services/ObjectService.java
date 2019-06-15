package com.ipb.platform.services;

import java.util.List;

import com.ipb.platform.dto.requests.ObjectRequestDTO;
import com.ipb.platform.dto.responses.ObjectResponseDTO;

public interface ObjectService {
	
	Long save(ObjectRequestDTO obj);
	
	List<ObjectResponseDTO> getAll();
	
	ObjectResponseDTO findById(Long id);
}
