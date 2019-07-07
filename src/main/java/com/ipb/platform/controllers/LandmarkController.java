package com.ipb.platform.controllers;

import java.util.List;

import com.ipb.platform.dto.responses.ObjectResponseDTO;
import com.ipb.platform.services.impl.LandmarkService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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
	public List<ObjectResponseDTO> getAll() {
		List<ObjectResponseDTO> result = service.getAll();
		return result;
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
