package com.ipb.platform.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ipb.platform.dto.requests.CityRequestDTO;
import com.ipb.platform.dto.requests.LandmarkRequestDTO;
import com.ipb.platform.dto.responses.CityResponseDTO;
import com.ipb.platform.dto.responses.LandmarkResponseDTO;
import com.ipb.platform.services.CityService;
import com.ipb.platform.services.LandmarkService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@CrossOrigin(origins = "*")
// @ControllerAdvice
// @RolesAllowed(value = { "ADMIN", "USER", "MODERATOR" })
@RequestMapping(path = "/cities/")
public class CityController {

	private CityService service;

	@RequestMapping(method = RequestMethod.GET)
	public List<CityResponseDTO> getAll() {
		List<CityResponseDTO> result = service.getAll();
		return result;
	}

	@RequestMapping(path = "id/{id}/", method = RequestMethod.GET)
	public CityResponseDTO getLandmarkById(@PathVariable Long id) {
		return service.findById(id);
	}

	@ResponseBody
	@RequestMapping(value = "create", method = RequestMethod.POST)
	public Long create(@RequestBody CityRequestDTO city) {
		return this.service.save(city);
	}
}
