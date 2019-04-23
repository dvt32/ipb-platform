package com.ipb.platform.services;

import java.util.List;

import com.ipb.platform.dto.requests.CityRequestDTO;
import com.ipb.platform.dto.responses.CityResponseDTO;

public interface CityService {
	
	boolean save(CityRequestDTO city);
	
	List<CityResponseDTO> getAll();
	
	CityResponseDTO findById(Long id);
	
}
