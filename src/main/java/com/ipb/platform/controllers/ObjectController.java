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

import com.ipb.platform.dto.requests.ObjectRequestDTO;
import com.ipb.platform.dto.responses.ObjectResponseDTO;
import com.ipb.platform.mappers.ImageMapper;
import com.ipb.platform.persistance.entities.ImageEntity;
import com.ipb.platform.persistance.entities.ObjectEntity;
import com.ipb.platform.services.ImageService;
import com.ipb.platform.services.ObjectService;

import lombok.AllArgsConstructor;

@CrossOrigin(origins = "*")
@AllArgsConstructor
@RestController
// @ControllerAdvice
// @RolesAllowed(value = { "ADMIN", "USER" })
@RequestMapping(path = "/objects/")
public class ObjectController {

	private ObjectService service;
	
	@RequestMapping(method = RequestMethod.GET)
	public List<ObjectResponseDTO> getAll() {
		List<ObjectResponseDTO> result = service.getAll();
		return result;
	}

	@RequestMapping(path = "id/{id}/", method = RequestMethod.GET)
	public ObjectResponseDTO getObjectById(@PathVariable Long id) {
		return service.findById(id);
	}

	@ResponseBody
	@RequestMapping(value = "create", method = RequestMethod.POST)
	public Long create(@RequestBody ObjectRequestDTO object) {
		return this.service.save(object);
	}
}
