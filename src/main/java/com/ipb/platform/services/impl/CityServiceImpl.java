package com.ipb.platform.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ipb.platform.dto.requests.CityRequestDTO;
import com.ipb.platform.dto.responses.CityResponseDTO;
import com.ipb.platform.mappers.CityMapper;
import com.ipb.platform.persistence.CityRepository;
import com.ipb.platform.persistence.entities.CityEntity;
import com.ipb.platform.persistence.entities.ObjectEntity;
import com.ipb.platform.services.CityService;
import com.ipb.platform.services.ImageService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CityServiceImpl implements CityService{
	
	private CityRepository repository;
	
	private CityMapper mapper;
	
	private ImageService imageService;
	
	@Override
	public Long save(CityRequestDTO city) {
		CityEntity entiity = this.mapper.toEntity(city);
		ObjectEntity objEntity = this.repository.save(entiity);
		
		// save all images
		this.imageService.save(city.getImages(), objEntity);
		
		return objEntity.getId();
	}

	@Override
	public List<CityResponseDTO> getAll() {
		return this.repository.findAll().stream()
				.map(entity -> this.mapper.toDTO(entity))
				.collect(Collectors.toList());
	}

	@Override
	public CityResponseDTO findById(Long id) {
		return this.mapper.toDTO(this.repository.findById(id).get());
	}
	

}
