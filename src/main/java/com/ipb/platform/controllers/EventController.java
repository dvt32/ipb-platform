package com.ipb.platform.controllers;

import java.util.List;

import com.ipb.platform.dto.responses.ObjectResponseDTO;
import com.ipb.platform.services.impl.EventService;
import org.springframework.web.bind.annotation.*;

import com.ipb.platform.dto.requests.EventRequestDTO;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/events/")
public class EventController {

	private EventService service;
	
	@RequestMapping(method = RequestMethod.GET)
	public List<ObjectResponseDTO> getAll(
			@RequestParam(defaultValue = "0", name = "page") int page,
		  @RequestParam(defaultValue = "15", name = "numberOfObjects") int numberOfObjects) {
		return service.getAll(page, numberOfObjects);
	}

	@RequestMapping(path = "id/{id}/", method = RequestMethod.GET)
	public ObjectResponseDTO getEventById(@PathVariable Long id) {
		return service.findById(id);
	}

	@ResponseBody
	@RequestMapping(value = "create", method = RequestMethod.POST)
	public Long create(@RequestBody EventRequestDTO event) {
		return this.service.save(event);
	}

	@ResponseBody
	@RequestMapping(value = "update/{id}", method = RequestMethod.PUT)
	public ObjectResponseDTO update(@PathVariable Long id, @RequestBody EventRequestDTO event) {
		return this.service.update(id, event);
	}

	@ResponseBody
	@RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
	public boolean deleteCategoryById(@PathVariable Long id) {
		this.service.deleteById(id);
		return true;
	}
}
