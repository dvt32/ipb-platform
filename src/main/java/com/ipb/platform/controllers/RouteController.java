package com.ipb.platform.controllers;

import com.ipb.platform.dto.CoordinatesDTO;
import com.ipb.platform.dto.requests.RouteRequestDTO;
import com.ipb.platform.dto.responses.RouteResponseDTO;
import com.ipb.platform.services.RouteService;
import com.ipb.platform.services.RouteService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/routes/")
public class RouteController {

	private RouteService service;

	@RequestMapping(method = RequestMethod.GET)
	public List<RouteResponseDTO> getAll() {
		return this.service.getAll();
	}

	@ResponseBody
	@RequestMapping(value = "create", method = RequestMethod.POST, consumes = "application/json")
	public Long create(@RequestBody RouteRequestDTO category) {
		return this.service.save(category);
	}

	@ResponseBody
	@RequestMapping(value = "update/{id}", method = RequestMethod.PUT, consumes = "application/json")
	public RouteResponseDTO update(@PathVariable Long id, @RequestBody RouteRequestDTO category) {
		return this.service.update(id, category);
	}

	@ResponseBody
	@RequestMapping(value = "id/{id}", method = RequestMethod.GET)
	public RouteResponseDTO getRouteById(@PathVariable Long id) {
		return this.service.findById(id);
	}

	@ResponseBody
	@RequestMapping(value = "id/{id}", method = RequestMethod.DELETE)
	public boolean deleteRouteById(@PathVariable Long id) {
		return this.service.deleteById(id);
	}
}
