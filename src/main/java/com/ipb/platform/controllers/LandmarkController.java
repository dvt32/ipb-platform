package com.ipb.platform.controllers;

import java.util.List;

import com.ipb.platform.dto.responses.ObjectResponseDTO;
import com.ipb.platform.services.impl.LandmarkService;
import org.springframework.web.bind.annotation.*;

import com.ipb.platform.dto.requests.LandmarkRequestDTO;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@CrossOrigin(origins = "*")
// @ControllerAdvice
// @RolesAllowed(value = { "ADMIN", "USER", "MODERATOR" })
@RequestMapping(path = "/landmarks/")
public class LandmarkController {

	private LandmarkService service;
	
	@RequestMapping(method = RequestMethod.GET)
	public List<ObjectResponseDTO> getAll(
			@RequestParam(defaultValue = "0", name = "page") int page,
			@RequestParam(defaultValue = "15", name = "numberOfObjects") int numberOfObjects
	) {
		return service.getAll(page, numberOfObjects);
	}

	@RequestMapping(path = "id/{id}/", method = RequestMethod.GET)
	public ObjectResponseDTO getLandmarkById(@PathVariable Long id) {
		return service.findById(id);
	}

	@ResponseBody
	@RequestMapping(value = "create", method = RequestMethod.POST)
	public Long create(@RequestBody LandmarkRequestDTO landmark) {
		return this.service.save(landmark);
	}

	@ResponseBody
	@RequestMapping(value = "update/{id}", method = RequestMethod.PUT)
	public ObjectResponseDTO update(@PathVariable Long id, @RequestBody LandmarkRequestDTO landmark) {
		return this.service.update(id, landmark);
	}

	@ResponseBody
	@RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
	public boolean delete(@PathVariable Long id) {
		this.service.deleteById(id);
		return true;
	}
}
