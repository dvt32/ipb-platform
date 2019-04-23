package com.ipb.platform.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ipb.platform.dto.requests.EventRequestDTO;
import com.ipb.platform.dto.responses.EventResponseDTO;
import com.ipb.platform.mappers.ImageMapper;
import com.ipb.platform.persistance.entities.ImageEntity;
import com.ipb.platform.persistance.entities.EventEntity;
import com.ipb.platform.services.ImageService;
import com.ipb.platform.services.EventService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
// @ControllerAdvice
// @RolesAllowed(value = { "ADMIN", "USER", "MODERATOR" })
@RequestMapping(path = "/events/")
public class EventController {

	private EventService service;
	
	@RequestMapping(method = RequestMethod.GET)
	public List<EventResponseDTO> getAll() {
		List<EventResponseDTO> result = service.getAll();
		return result;
	}

	@RequestMapping(path = "id/{id}/", method = RequestMethod.GET)
	public EventResponseDTO getEventById(@PathVariable Long id) {
		return service.findById(id);
	}

	@ResponseBody
	@RequestMapping(value = "create", method = RequestMethod.POST)
	public ResponseEntity<String> login(@RequestBody EventRequestDTO event) {	
		this.service.save(event);
		ResponseEntity<String> responseEntity = new ResponseEntity<String>(new String("created"), HttpStatus.OK);
		return responseEntity;
	}
}
