package com.ipb.platform.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ipb.platform.dto.requests.LandmarkRequestDTO;
import com.ipb.platform.dto.responses.LandmarkResponseDTO;
import com.ipb.platform.mappers.ImageMapper;
import com.ipb.platform.mappers.LandmarkMapper;
import com.ipb.platform.persistence.CityRepository;
import com.ipb.platform.persistence.ImageRepository;
import com.ipb.platform.persistence.LandmarkRepository;
import com.ipb.platform.persistence.entities.CityEntity;
import com.ipb.platform.persistence.entities.ImageEntity;
import com.ipb.platform.persistence.entities.LandmarkEntity;
import com.ipb.platform.persistence.entities.ObjectEntity;
import com.ipb.platform.services.ImageService;
import com.ipb.platform.services.LandmarkService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class LandmarkServiceImpl implements LandmarkService {
	
	private LandmarkRepository repository;
	private CityRepository cityRepository;
	
	private LandmarkMapper mapper;
	
	private ImageService imageService;
	
	@Override
	public Long save(LandmarkRequestDTO landmark) {
		Optional<CityEntity> city = this.cityRepository.findById(landmark.getCityId());
		
		if (!city.isPresent()) {
			throw new IllegalArgumentException("Tryed to add a landmark for non-existant city.");
		}
		
		LandmarkEntity entiity = this.mapper.toEntity(landmark);
		entiity.setCity(city.get());
	
		ObjectEntity objEntity = this.repository.save(entiity);
		
		// save all images
		this.imageService.save(landmark.getImages(), objEntity);
				
		return objEntity.getId();
	}

	@Override
	public List<LandmarkResponseDTO> getAll() {
		return this.repository.findAll().stream()
				.map(entity -> this.mapper.toDTO(entity))
				.collect(Collectors.toList());
	}

	@Override
	public LandmarkResponseDTO findById(Long id) {
		return this.mapper.toDTO(this.repository.findById(id).get());
	}

}
