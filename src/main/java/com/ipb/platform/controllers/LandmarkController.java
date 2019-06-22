package com.ipb.platform.controllers;

import java.util.List;
import java.util.stream.Collectors;

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

import com.ipb.platform.dto.requests.LandmarkRequestDTO;
import com.ipb.platform.dto.responses.LandmarkResponseDTO;
import com.ipb.platform.mappers.ImageMapper;
import com.ipb.platform.persistence.entities.ImageEntity;
import com.ipb.platform.persistence.entities.ObjectEntity;
import com.ipb.platform.services.ImageService;
import com.ipb.platform.services.LandmarkService;

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
	public List<LandmarkResponseDTO> getAll() {
		List<LandmarkResponseDTO> result = service.getAll();
		return result;
	}

	@RequestMapping(path = "id/{id}/", method = RequestMethod.GET)
	public LandmarkResponseDTO getLandmarkById(@PathVariable Long id) {
		return service.findById(id);
	}

	@ResponseBody
	@RequestMapping(value = "create", method = RequestMethod.POST)
	public Long create(@RequestBody LandmarkRequestDTO landmark) {
		return this.service.save(landmark);
	}
}
