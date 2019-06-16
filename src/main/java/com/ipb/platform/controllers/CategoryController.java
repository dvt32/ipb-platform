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
	}

	@ResponseBody
	@RequestMapping(value = "update/{id}", method = RequestMethod.POST, consumes = "application/json")
	public CategoryResponseDTO update(@PathVariable Long id, @RequestBody CategoryRequestDTO category) {
		return this.service.update(id, category);
	}

	@ResponseBody
	@RequestMapping(value = "id/{id}", method = RequestMethod.GET)
	public CategoryResponseDTO getCategoryById(@PathVariable Long id) {
		return this.service.findById(id);
	}

	@ResponseBody
	@RequestMapping(value = "parentId/{id}", method = RequestMethod.GET)
	public List<CategoryResponseDTO> getChildrensByParentId(@PathVariable Long id) {
		return this.service.getChildrenByParentId(id);
	}

	@ResponseBody
	@RequestMapping(value = "delete/id/{id}", method = RequestMethod.DELETE)
	public boolean deleteCategoryById(@PathVariable Long id) {
		return this.service.deleteById(id);
	}
}
