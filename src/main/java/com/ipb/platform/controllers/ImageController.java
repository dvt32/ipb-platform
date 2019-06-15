package com.ipb.platform.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ipb.platform.dto.requests.ImageRequestDTO;
import com.ipb.platform.dto.responses.ImageResponseDTO;
import com.ipb.platform.services.ImageService;

import lombok.AllArgsConstructor;

@CrossOrigin(origins = "*")
@AllArgsConstructor
@RestController
// @ControllerAdvice
// @RolesAllowed(value = { "ADMIN", "USER", "MODERATOR" })
@RequestMapping(path = "/images/")
public class ImageController {

	private ImageService service;

	@RequestMapping(path = "objectId/{id}/", method = RequestMethod.GET)
	public List<ImageResponseDTO> getImageByObjectId(@PathVariable Long id) {
		return service.findByObjectId(id);
	}

	@RequestMapping(path = "id/{id}/", method = RequestMethod.GET)
	public ImageResponseDTO getImageById(@PathVariable Long id) {
		return service.findById(id);
	}

	@ResponseBody
	@RequestMapping(value = "create", method = RequestMethod.POST)
	public Long create(@RequestBody ImageRequestDTO image) {
		return this.service.save(image);
	}
}
