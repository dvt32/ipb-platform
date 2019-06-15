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

import com.ipb.platform.dto.requests.CategoryRequestDTO;
import com.ipb.platform.dto.responses.CategoryResponseDTO;
import com.ipb.platform.mappers.ImageMapper;
import com.ipb.platform.persistance.entities.ImageEntity;
import com.ipb.platform.persistance.entities.ObjectEntity;
import com.ipb.platform.services.ImageService;
import com.ipb.platform.services.CategoryService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@CrossOrigin(origins = "*")
// @ControllerAdvice
// @RolesAllowed(value = { "ADMIN", "USER", "MODERATOR" })
@RequestMapping(path = "/categories/")
public class CategoryController {

	private CategoryService service;
	
	@RequestMapping(method = RequestMethod.GET)
	public List<CategoryResponseDTO> getAll() {
		return this.service.getAll();
	}

	@ResponseBody
	@RequestMapping(value = "create", method = RequestMethod.POST, consumes = "application/json")
	public Long create(@RequestBody CategoryRequestDTO category) {
		return this.service.save(category);
//		ResponseEntity<String> responseEntity = new ResponseEntity<String>(new String("created"), HttpStatus.OK);
//		return responseEntity;
	}
}
