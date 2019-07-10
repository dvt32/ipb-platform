package com.ipb.platform.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ipb.platform.dto.requests.EventRequestDTO;
import com.ipb.platform.dto.requests.LandmarkRequestDTO;
import com.ipb.platform.dto.responses.EventResponseDTO;
import com.ipb.platform.dto.responses.LandmarkResponseDTO;
import com.ipb.platform.mappers.EventMapper;
import com.ipb.platform.mappers.ImageMapper;
import com.ipb.platform.mappers.LandmarkMapper;
import com.ipb.platform.persistence.CityRepository;
import com.ipb.platform.persistence.EventRepository;
import com.ipb.platform.persistence.ImageRepository;
import com.ipb.platform.persistence.LandmarkRepository;
import com.ipb.platform.persistence.entities.CityEntity;
import com.ipb.platform.persistence.entities.EventEntity;
import com.ipb.platform.persistence.entities.ImageEntity;
import com.ipb.platform.persistence.entities.LandmarkEntity;
import com.ipb.platform.persistence.entities.ObjectEntity;
import com.ipb.platform.services.EventService;
import com.ipb.platform.services.ImageService;
import com.ipb.platform.services.LandmarkService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class EventServiceImpl implements EventService {
	
	private EventRepository repository;
	private CityRepository cityRepository;
	
	private EventMapper mapper;
	
	private ImageService imageService;
	
	@Override
	public Long save(EventRequestDTO event) {
		Optional<CityEntity> city = this.cityRepository.findById(event.getCityId());
		
		if (!city.isPresent()) {
			throw new IllegalArgumentException("Tryed to add a landmark for non-existant city.");
		}
		
		EventEntity entiity = this.mapper.toEntity(event);
		entiity.setCity(city.get());
	
		ObjectEntity objEntity = this.repository.save(entiity);
		
		// save all images
		this.imageService.save(event.getImages(), objEntity);
				
		return objEntity.getId();
	}

	@Override
	public List<EventResponseDTO> getAll() {
		return this.repository.findAll().stream()
				.map(entity -> this.mapper.toDTO(entity))
				.collect(Collectors.toList());
	}

	@Override
	public EventResponseDTO findById(Long id) {
		return this.mapper.toDTO(this.repository.findById(id).get());
	}

}
