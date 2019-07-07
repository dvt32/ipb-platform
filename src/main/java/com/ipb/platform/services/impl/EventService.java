package com.ipb.platform.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.ipb.platform.dto.requests.ObjectRequestDTO;
import com.ipb.platform.dto.responses.ObjectResponseDTO;
import com.ipb.platform.exceptions.IllegalRequestArgumentException;
import com.ipb.platform.mappers.impl.EventMapper;
import com.ipb.platform.persistance.entities.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ipb.platform.dto.requests.EventRequestDTO;
import com.ipb.platform.persistance.EventRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class EventService extends IPBObjectServiceImpl {
	
	private EventRepository repository;

	private EventMapper mapper;
	
	private CityService cityService;

	@Override
	public Long save(ObjectRequestDTO object) {

		EventRequestDTO event = this.getEventRequestDTOInstanceOrThrow(object);
		CityEntity city =
				this.cityService.getCityEntityOrThrow(
						event.getCityId(),
						"Tryed to add a event for non-existant city."
				);

		this.checkEventFields(event);
		
		EventEntity entity = this.mapper.toEntity(event);
		entity.setCity(city);

		super.setImages(event, entity);
		super.setCategories(event, entity);

		ObjectEntity objEntity = this.repository.save(entity);
		return objEntity.getId();
	}

	@Override
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

		super.update(id, event);

		return this.mapper.toDTO(this.repository.save(objEntity));
	}

	@Override
	public List<ObjectResponseDTO> getAll() {
		return this.repository.findAll().stream()
				.map(entity -> this.mapper.toDTO(entity))
				.collect(Collectors.toList());
	}

	@Override
	public ObjectResponseDTO findById(Long id) {
		return this.mapper.toDTO(this.repository.findById(id).get());
	}

	@Override
	public void deleteById(Long id) {
		super.deleteById(id);
	}

	private EventEntity getEventEntityOrThrow(Long eventId, String errorMessage) {

		Optional<EventEntity> object = this.repository.findById(eventId);

		if (!object.isPresent()) {
			throw new IllegalRequestArgumentException(errorMessage);
		}

		return object.get();
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
