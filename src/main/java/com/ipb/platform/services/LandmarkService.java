package com.ipb.platform.services;

import java.util.List;

import com.ipb.platform.dto.requests.LandmarkRequestDTO;
import com.ipb.platform.dto.responses.LandmarkResponseDTO;

public interface LandmarkService {
	
	Long save(LandmarkRequestDTO landmark);
	
	List<LandmarkResponseDTO> getAll();
	
	LandmarkResponseDTO findById(Long id);
}
