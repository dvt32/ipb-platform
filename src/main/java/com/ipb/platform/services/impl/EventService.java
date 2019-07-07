package com.ipb.platform.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.ipb.platform.dto.CoordinatesDTO;
import com.ipb.platform.dto.requests.ObjectRequestDTO;
import com.ipb.platform.dto.responses.ObjectResponseDTO;
import com.ipb.platform.exceptions.IllegalRequestArgumentException;
import com.ipb.platform.mappers.impl.EventMapper;
import com.ipb.platform.persistence.entities.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ipb.platform.dto.requests.EventRequestDTO;
import com.ipb.platform.persistence.EventRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class EventService extends IPBObjectServiceImpl {
	
	private EventRepository repository;

	private EventMapper mapper;
	
	private CityService cityService;

	public Long save(ObjectRequestDTO object) {

		EventRequestDTO event = this.getEventRequestDTOInstanceOrThrow(object);

		this.checkForExistEventByName(event.getName());

		CityEntity city =
				this.cityService.getCityEntityOrThrow(
						event.getCityId(),
						"Tryed to add a event for non-existant city."
				);

		this.checkEventFields(event);
		
		EventEntity entity = this.mapper.toEntity(event);
		entity.setCity(city);
		super.setCreator(entity);
		super.setImages(event, entity);
		super.setCategories(event, entity);

		ObjectEntity objEntity = this.repository.save(entity);
		return objEntity.getId();
	}

	public ObjectResponseDTO update(Long id, ObjectRequestDTO object) {
		EventEntity objEntity =
				this.getEventEntityOrThrow(id, "Tryed to update non-existant event.");

		EventRequestDTO event = this.getEventRequestDTOInstanceOrThrow(object);

		CityEntity city =
				this.cityService.getCityEntityOrThrow(
						event.getCityId(),
						"Tryed to add a event for non-existant city."
				);

		this.checkEventFields(event);

		objEntity.setStartDate(event.getStartDate().getTime());
		objEntity.setEndDate(event.getEndDate().getTime());
		objEntity.setWorkTime(event.getWorkTime());
		objEntity.setCity(city);

		super.update(id, event, this.mapper);

		return this.mapper.toDTO(this.repository.save(objEntity));
	}

	public List<ObjectResponseDTO> getAll(int page, int numberOfObjects) {
		Pageable pageable = PageRequest.of(page, numberOfObjects);
		return this.repository.findAll(pageable).stream()
				.map(entity -> this.mapper.toDTO(entity))
				.collect(Collectors.toList());
	}

	public ObjectResponseDTO findById(Long id) {
		return this.mapper.toDTO(this.repository.findById(id).get());
	}

	@Override
	public void deleteById(Long id) {
		super.deleteById(id);
	}

	public List<ObjectResponseDTO> findAllAroundCoordinates(List<CoordinatesDTO> coordinates, int numberOfObjects){
		return super.findAllAroundCoordinates(coordinates, numberOfObjects, ObjectType.EVENT, this.mapper);
	}

	private EventEntity getEventEntityOrThrow(Long eventId, String errorMessage) {

		Optional<EventEntity> object = this.repository.findById(eventId);

		if (!object.isPresent()) {
			throw new IllegalRequestArgumentException(errorMessage);
		}

		return object.get();
	}

	private void checkForExistEventByName(String name){
		List<EventEntity> eventsByName =
				this.repository.findAllByName(name);
		if (eventsByName.size() > 0) {
			throw new IllegalArgumentException("Event with name '"+ name +"' exists!");
		}
	}

	private void checkEventFields(EventRequestDTO event) {

		if (event.getStartDate() == null || event.getEndDate() == null) {
			throw new IllegalRequestArgumentException("Tryed to add a event with empty stat date or end date.");
		}
	}

	private EventRequestDTO getEventRequestDTOInstanceOrThrow(ObjectRequestDTO object) {
		if (!(object instanceof EventRequestDTO))
		{
			throw new IllegalArgumentException("Incorrect Event");
		}

		return ((EventRequestDTO) object) ;
	}
}
