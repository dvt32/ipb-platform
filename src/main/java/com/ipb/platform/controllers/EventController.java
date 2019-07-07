package com.ipb.platform.controllers;

import java.util.List;

import com.ipb.platform.dto.responses.ObjectResponseDTO;
import com.ipb.platform.services.impl.EventService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ipb.platform.dto.requests.EventRequestDTO;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/events/")
public class EventController {

	private EventService service;
	
	@RequestMapping(method = RequestMethod.GET)
	public List<ObjectResponseDTO> getAll() {
		List<ObjectResponseDTO> result = service.getAll();
		return result;
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
