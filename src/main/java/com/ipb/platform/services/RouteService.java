package com.ipb.platform.services;

import com.ipb.platform.dto.CoordinatesDTO;
import com.ipb.platform.dto.requests.RouteRequestDTO;
import com.ipb.platform.dto.responses.RouteResponseDTO;
import com.ipb.platform.dto.responses.RouteResponseDTO;

import java.util.List;

public interface RouteService {

	/**
	 *
	 * @param route contains all the data for the new route
	 * @return route id which is generated automatically
	 */
	Long save(RouteRequestDTO route);

	/**
	 *
	 * @param routeId contain route id we want to edit
	 * @param route contain new route data
	 * @return update route
	 */
	RouteResponseDTO update(Long routeId, RouteRequestDTO route);

	/**
	 *
	 * @return List<RouteResponseDTO> which contains all categories information
	 */
	List<RouteResponseDTO> getAll();

	List<RouteResponseDTO> findByUserId(Long userId);
	
	RouteResponseDTO findById(Long id);

	boolean deleteById(Long id);

}
