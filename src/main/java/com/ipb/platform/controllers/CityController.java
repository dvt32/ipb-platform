package com.ipb.platform.controllers;

import java.util.List;

import com.ipb.platform.dto.responses.ObjectResponseDTO;
import com.ipb.platform.services.impl.CityService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import com.ipb.platform.dto.requests.CityRequestDTO;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/cities/")
//@Secured("ROLE_ADMIN")
public class CityController {

	private CityService service;

	@RequestMapping(path = "all",method = RequestMethod.GET)
	public List<ObjectResponseDTO> getAll() {
		return service.getAll();
	}

	@RequestMapping(method = RequestMethod.GET)
	public List<ObjectResponseDTO> getAll(
			@RequestParam(defaultValue = "0", name = "page") int page,
			@RequestParam(defaultValue = "15", name = "numberOfObjects") int numberOfObjects) {
		return service.getAll(page, numberOfObjects);
	}

	@RequestMapping(path = "id/{id}/", method = RequestMethod.GET)
	public ObjectResponseDTO getById(@PathVariable Long id) {
		return service.findById(id);
	}

	@ResponseBody
	@RequestMapping(value = "update/{id}", method = RequestMethod.PUT)
	public ObjectResponseDTO update(@PathVariable Long id, @RequestBody CityRequestDTO city) {
		return this.service.update(id, city);
	}

	@ResponseBody
	@RequestMapping(value = "create", method = RequestMethod.POST,
			consumes = "application/json", produces = "application/json")
	public Long create(@RequestBody CityRequestDTO city) {
		return this.service.save(city);
	}

	@ResponseBody
	@RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
	public boolean deleteCategoryById(@PathVariable Long id) {
		this.service.deleteById(id);
		return true;
	}
}
