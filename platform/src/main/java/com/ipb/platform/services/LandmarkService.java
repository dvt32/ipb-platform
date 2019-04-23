package com.ipb.platform.services;

import java.util.List;

import com.ipb.platform.dto.requests.LandmarkRequestDTO;
import com.ipb.platform.dto.responses.LandmarkResponseDTO;

public interface LandmarkService {
	
	boolean save(LandmarkRequestDTO landmark);
	
	List<LandmarkResponseDTO> getAll();
	
	LandmarkResponseDTO findById(Long id);
}
