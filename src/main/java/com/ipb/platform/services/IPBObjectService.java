package com.ipb.platform.services;

import java.util.List;

import com.ipb.platform.dto.requests.ObjectRequestDTO;
import com.ipb.platform.dto.responses.ObjectResponseDTO;
import com.ipb.platform.persistance.entities.ObjectEntity;

public interface IPBObjectService {
	
	Long save(ObjectRequestDTO obj);

	ObjectResponseDTO update(Long id, ObjectRequestDTO obj);

	List<ObjectResponseDTO> getAll();
	
	ObjectResponseDTO findById(Long id);

	void deleteById(Long id);
}
